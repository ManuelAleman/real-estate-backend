const estateModel = require("../models/estateModel");
const sellerModel = require("../models/sellerModel");
const userModel = require("../models/userModel");
const fs = require("fs");
const path = require("path");
//CREATE ESTATE
const createEstateController = async (req, res) => {
  try {
    const characteristics = req.body.characteristics
      .split(",")
      .map((c) => c.trim());

    const {
      name,
      presentationImg,
      description,
      price,
      type,
      category,
      user,
      city,
      address,
      wantSeller,
      status,
    } = req.body;

    if (
      !name ||
      !presentationImg ||
      !description ||
      !price ||
      !category ||
      !user ||
      !city ||
      !address ||
      !status ||
      !characteristics
    ) {
      return res.status(500).send({
        success: false,
        message: "Please fill all the fields",
      });
    }

    if (!req.files || Object.keys(req.files).length === 0) {
      return res.status(400).send("No files were uploaded.");
    }

    if (req.files.images.length > characteristics.length * 2) {
      return res.status(400).send({
        success: false,
        message: "Please upload min 2 images for each characteristic",
      });
    }

    const seller = req.body.seller || null;
    const paths = await guardarImagenes(req.files.images, category, user);

    const userInfo = await userModel.findById(user);
    if (userInfo && userInfo.role !== "seller") {
      await userModel.findByIdAndUpdate(user, { $set: { role: "seller" } });
    }

    const newEstate = new estateModel({
      name,
      presentationImg,
      description,
      price,
      type,
      category,
      user,
      seller,
      wantSeller,
      city,
      address,
      status,
      characteristics,
      images: paths,
    });

    await newEstate.save();
    res.status(201).send({
      success: true,
      message: "Estate created successfully",
      estate: newEstate,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Error creating estate",
    });
  }
};

const getEstateInfoController = async (req, res) => {
  try {
    const estate = await estateModel.findById(req.params.id);
    if (!estate) {
      return res.status(404).send({
        success: false,
        message: "Estate not found",
      });
    }
    res.status(200).send({
      success: true,
      estate,
    });
  } catch (error) {
    res.status(500).send({
      success: false,
      message: "Error getting estate",
    });
  }
};

const getEstateController = async (req, res) => {
  try {
    const estates = await estateModel.find();
    res.status(200).send({
      success: true,
      estates,
    });
  } catch (error) {
    res.status(500).send({
      success: false,
      message: "Error getting estates",
    });
  }
};

const guardarImagenes = async (imagenes, category, seller) => {
  const directorioSeller = "./images/seller/" + seller;

  if (!fs.existsSync(directorioSeller)) {
    fs.mkdirSync(directorioSeller, { recursive: true });
  }

  const casaId = Date.now().toString();

  const directorioCasa = path.join(directorioSeller, casaId);

  if (!fs.existsSync(directorioCasa)) {
    fs.mkdirSync(directorioCasa);
  }

  const paths = [];

  for (const imagen of imagenes) {
    const nombreImagen = Date.now() + "_" + imagen.name;
    const rutaImagen = path.join(directorioCasa, nombreImagen);
    await imagen.mv(rutaImagen);
    paths.push(rutaImagen);
  }

  return paths;
};

const getEstatesFromUser = async (req, res) => {
  try {
    const estates = await estateModel.find({ user: req.params.id });

    res.status(201).send({
      success: true,
      message: "Estates found",
      estates,
    });
  } catch (error) {
    res.status(500).send({
      success: false,
      message: "Error getting estates",
    });
  }
};

const approveEstate = async (req, res) => {
  try {
    const estate = await estateModel.findById(req.params.id);
    if (!estate) {
      return res.status(404).send({
        success: false,
        message: "Estate not found",
      });
    }
    estate.status = "approved";
    await estate.save();
    res.status(200).send({
      success: true,
      message: "Estate approved",
    });
  } catch (error) {
    res.status(500).send({
      success: false,
      message: "Error approving estate",
    });
  }
};

const getNoApprovedEstates = async (req, res) => {
  try {
    const estates = await estateModel.find({ status: "waiting" });
    res.status(200).send({
      success: true,
      estates,
    });
  } catch (error) {
    res.status(500).send({
      success: false,
      message: "Error getting estates",
    });
  }
};

const assignSellerController = async (req, res) => {
  try {
    console.log(req.body);
    const seller = await sellerModel.findById(req.body.sellerId);
    if (!seller) {
      return res.status(404).send({
        success: false,
        message: "Seller not found",
      });
    }

    const estate = await estateModel.findById(req.body.estateId);
    if (!estate) {
      return res.status(404).send({
        success: false,
        message: "Estate not found",
      });
    }

    estate.seller = seller._id;
    estate.wantSeller = false;
    await estate.save();
    res.status(200).send({
      success: true,
      message: "Seller assigned",
    });
  } catch (error) {
    res.status(500).send({
      success: false,
      message: "Error assigning seller",
    });
  }
};

module.exports = {
  createEstateController,
  getEstateController,
  getEstateInfoController,
  getEstatesFromUser,
  approveEstate,
  getNoApprovedEstates,
  assignSellerController,
};

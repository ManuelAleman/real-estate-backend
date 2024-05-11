const estateModel = require("../models/estateModel");
const sellerModel = require("../models/sellerModel");
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
      category,
      city,
      address,
      status,
    } = req.body;

    const images = [];

    if (
      !name ||
      !presentationImg ||
      !description ||
      !price ||
      !category ||
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

    const seller_id = req.body.id;
    const paths = await guardarImagenes(req.files.images, category, seller_id);

    const newEstate = new estateModel({
      name,
      presentationImg,
      description,
      price,
      category,
      seller: seller_id,
      city,
      address,
      status,
      characteristics,
      images: paths,
    });

    //create a new seller instance for the user in case they dont want to have a verified seller
    const newSeller = new sellerModel({
      user: seller_id,
      location: address,
      city,
      verified: false,
    });

    await newSeller.save();

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

module.exports = {
  createEstateController,
  getEstateController,
  getEstateInfoController,
};

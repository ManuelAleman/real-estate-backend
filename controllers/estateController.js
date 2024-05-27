const estateModel = require("../models/estateModel");
const sellerModel = require("../models/sellerModel");
const userModel = require("../models/userModel");
const categoryModel = require("../models/categoryModel");
const fs = require("fs");
const path = require("path");
//CREATE ESTATE
const createEstateController = async (req, res) => {
  try {
    const {
      name,
      description,
      price,
      type,
      category,
      user,
      city,
      address,
      wantSeller,
      status,
      characteristics,
    } = req.body;

    const presentationImg = req.files.presentationImg;
    const images = req.files.images;

    const presentationImgPath = "/images/" + presentationImg[0].filename;
    const imagesPath = images.map((img) => "/images/" + img.filename);

    if (
      !name ||
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

    const categoryExists = await categoryModel.find({ name: category });
    if (!categoryExists) {
      return res.status(404).send({
        success: false,
        message: "Category not found",
      });
    }

    const categoryId = categoryExists[0]._id;

    const seller = req.body.seller || null;

    const newEstate = new estateModel({
      name,
      presentationImg: presentationImgPath,
      description,
      price,
      type,
      category: categoryId,
      user,
      seller,
      wantSeller,
      city,
      address,
      status,
      characteristics,
      images: imagesPath,
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

const getEstatesApproved = async (req, res) => {
  try {
    const estates = await estateModel.find({ status: "approved" });
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
  getEstatesApproved,
};

const sellerModel = require("../models/sellerModel");
const userModel = require("../models/userModel");

//CREATE SELLER
const createSellerController = async (req, res) => {
  try {
    //find user
    const user = await userModel.findById({ _id: req.body._id });
    if (!user) {
      return res.status(404).send({
        success: false,
        message: "User not found",
      });
    }

    user.role = "seller";
    await user.save();
    const { city } = req.body;
    const seller = new sellerModel({ user: user._id, city });
    seller.verified = true;
    await seller.save();
    res.status(200).send({
      success: true,
      message: "Seller Created",
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in CREATE SELLER API",
      error,
    });
  }
};

const getSellerById = async (req, res) => {
  try {
    const seller = await sellerModel
      .findById(req.params.id)
      .populate("user")
      .exec();
    if (!seller) {
      return res.status(404).send({
        success: false,
        message: "Seller not found",
      });
    }

    res.status(200).send({
      success: true,
      message: "Seller",
      seller,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in GET SELLER BY ID API",
      error,
    });
  }
};

const getSellerByUserId = async (req, res) => {
  try {
    const seller = await sellerModel
      .findOne({ user: req.params.id })
      .populate("user")
      .exec();
    if (!seller) {
      return res.status(404).send({
        success: false,
        message: "Seller not found",
      });
    }

    res.status(200).send({
      success: true,
      message: "Seller",
      seller,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in GET SELLER BY USER ID API",
      error,
    });
  }
};

module.exports = { createSellerController, getSellerById, getSellerByUserId };

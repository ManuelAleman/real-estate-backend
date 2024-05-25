const userModel = require("../models/userModel");
const bcrypt = require("bcryptjs");
const sellerModel = require("../models/sellerModel");
//GET USER INFO
const getUserController = async (req, res) => {
  try {
    //find user
    const user = await userModel.findById({ _id: req.body.id });
    if (!user) {
      return res.status(404).send({
        success: false,
        message: "User not found",
      });
    }
    //hinde password
    user.password = undefined;
    //resp
    res.status(200).send({
      success: true,
      message: "User found",
      user,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

//UPDATE USER
const updateUserController = async (req, res) => {
  try {
    //find user
    const user = await userModel.findById({ _id: req.body.id });
    if (!user) {
      return res.status(404).send({
        success: false,
        message: "User not found",
      });
    }

    //update user
    const { name, contactNumber } = req.body;
    if (name) user.name = name;
    if (contactNumber) user.contactNumber = contactNumber;

    //save user
    await user.save();
    res.status(200).send({
      success: true,
      message: "User Updated",
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in UPDATE API",
      error,
    });
  }
};

//RESET PASSWORD CONTROLLER
const resetPasswordController = async (req, res) => {
  try {
    //validate
    const { email, newPassword, answer } = req.body;
    if (!email || !newPassword || !answer) {
      return res.status(500).send({
        success: false,
        message: "Please fill all the fields",
      });
    }
    const user = await userModel.findOne({ email, answer });

    if (!user.answer === answer) {
      return res.status(500).send({
        success: false,
        message: "Invalid answer",
      });
    }

    //check if user already exists
    if (!user) {
      return res.status(500).send({
        success: false,
        message: "User not found or invalid answer",
      });
    }
    //hash password
    const salt = bcrypt.genSaltSync(10);
    const hashedPassword = await bcrypt.hash(newPassword, salt);
    //update password
    user.password = hashedPassword;
    //save user
    await user.save();
    res.status(200).send({
      success: true,
      message: "Password Updated Successfully",
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in RESET PASSWORD API",
      error,
    });
  }
};

//UPDATE PASSWORD FROM USER
const updatePasswordController = async (req, res) => {
  try {
    //find user
    const user = await userModel.findById({ _id: req.body.id });
    //valdiation
    if (!user) {
      return res.status(404).send({
        success: false,
        message: "Usre Not Found",
      });
    }
    // get data from user
    const { oldPassword, newPassword } = req.body;
    if (!oldPassword || !newPassword) {
      return res.status(500).send({
        success: false,
        message: "Please Provide Old or New PasswOrd",
      });
    }
    //check user password  | compare password
    const isMatch = await bcrypt.compare(oldPassword, user.password);
    if (!isMatch) {
      return res.status(500).send({
        success: false,
        message: "Invalid old password",
      });
    }
    //hashing password
    var salt = bcrypt.genSaltSync(10);
    const hashedPassword = await bcrypt.hash(newPassword, salt);
    user.password = hashedPassword;
    await user.save();
    res.status(200).send({
      success: true,
      message: "Password Updated!",
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Error In Password Update API",
      error,
    });
  }
};

//DELETE PROFILE
const deleteProfileController = async (req, res) => {
  try {
    await userModel.findByIdAndDelete(req.params.id);
    return res.status(200).send({
      success: true,
      message: "Your account has been deleted",
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in DELETE API",
      error,
    });
  }
};

//ADMIN FEATURES
const setVerifiedSellerController = async (req, res) => {
  try {
    const seller = await sellerModel.findById({ _id: req.body.user.id });
    if (!seller) {
      return res.status(404).send({
        success: false,
        message: "Seller not found",
      });
    }

    //update seller
    seller.verified = true;
    await seller.save();
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in SET VERIFIED SELLER API",
      error,
    });
  }
};

const getVerifiedSellersController = async (req, res) => {
  try {
    const sellers = await sellerModel
      .find({ verified: true })
      .populate("user")
      .exec();

    console.log(sellers);

    if (!sellers.length) {
      return res.status(404).send({
        success: false,
        message: "No verified sellers found",
      });
    }

    res.status(200).send({
      success: true,
      message: "Verified Sellers",
      sellers,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in GET VERIFIED SELLERS API",
      error,
    });
  }
};

const getUserInfoFromSeller = async (req, res) => {
  try {
    const user = await userModel.findById({ _id: req.body.id });
    if (!user) {
      return res.status(404).send({
        success: false,
        message: "User not found",
      });
    }

    //hinde password
    user.password = undefined;
    //resp
    res.status(200).send({
      success: true,
      message: "User found",
      user,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

const getAllUsers = async (req, res) => {
  try {
    const users = await userModel.find({ role: "user" });
    if (!users.length) {
      return res.status(404).send({
        success: false,
        message: "No users found",
      });
    }
    res.status(200).send({
      success: true,
      message: "All Users",
      users,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in GET ALL USERS API",
      error,
    });
  }
};

module.exports = {
  getUserController,
  updateUserController,
  resetPasswordController,
  updatePasswordController,
  deleteProfileController,
  setVerifiedSellerController,
  getVerifiedSellersController,
  getUserInfoFromSeller,
  getAllUsers,
};

const userModel = require("../models/userModel");
const bcrypt = require("bcryptjs");
const JWT = require("jsonwebtoken");

const registerController = async (req, res) => {
  try {
    const { name, contactNumber, email, password, answer } = req.body;
    //validate
    if (!name || !contactNumber || !email || !password || !answer) {
      return res.status(500).json({
        success: false,
        message: "Please fill all the fields",
      });
    }

    //check if user already exists
    const existingUser = await userModel.findOne({ email });
    if (existingUser) {
      return res.status(500).json({
        success: false,
        message: "User already registered please login",
      });
    }

    //hash password
    const salt = bcrypt.genSaltSync(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    //create new User
    const user = await userModel.create({
      name,
      contactNumber,
      email,
      password: hashedPassword,
      answer,
    });
    res.status(201).json({
      success: true,
      message: "User Registered Successfully",
      user,
    });
  } catch (error) {
    console.log(error);
    res.status(500).json({
      success: false,
      message: "Internal Server Error in Register API",
      error,
    });
  }
};

const loginController = async (req, res) => {
  try {
    const { email, password } = req.body;
    //validate
    if (!email || !password) {
      return res.status(500).send({
        success: false,
        message: "Please fill all the fields",
      });
    }

    //check if user already exists
    const user = await userModel.findOne({ email: email });
    if (!user) {
      return res.status(500).send({
        success: false,
        message: "User not found, please register first",
      });
    }

    //check password
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(500).send({
        success: false,
        message: "Invalid Credentials",
      });
    }

    const token = JWT.sign({ id: user._id }, process.env.JWT_SECRET, {
      expiresIn: "7d",
    });

    user.password = undefined;
    res.status(200).send({
      success: true,
      message: "User Logged In Successfully",
      token,
      user,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in Login API",
      error,
    });
  }
};

module.exports = { registerController, loginController };

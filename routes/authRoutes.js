const express = require("express");
const {
  registerController,
  loginController,
} = require("../controllers/authController");

const router = express.Router();

//RESGISTER / POST
router.post("/signup", registerController);

//LOGIN / POST
router.post("/login", loginController);

module.exports = router;

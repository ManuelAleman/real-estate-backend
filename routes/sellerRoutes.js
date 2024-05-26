const express = require("express");
const {
  createSellerController,
  getSellerById,
} = require("../controllers/sellerController");
const authMiddleware = require("../middleware/authMiddleware");
const adminMiddleware = require("../middleware/adminMiddleware");

const router = express.Router();

router.post("/addSeller", authMiddleware, createSellerController);

router.get("/getSellerById/:id", getSellerById);

module.exports = router;

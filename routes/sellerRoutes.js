const express = require("express");
const { createSellerController } = require("../controllers/sellerController");
const authMiddleware = require("../middleware/authMiddleware");
const adminMiddleware = require("../middleware/adminMiddleware");

const router = express.Router();

router.post(
  "/addSeller",
  authMiddleware,
  adminMiddleware,
  createSellerController
);

module.exports = router;

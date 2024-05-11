const express = require("express");
const authMiddleware = require("../middleware/authMiddleware");
const {
  createEstateController,
  getEstateController,
  getEstateInfoController,
} = require("../controllers/estateController");

const router = express.Router();

router.post("/createEstate", authMiddleware, createEstateController);

router.get("/getEstates", getEstateController);

router.get("/getEstateInfo/:id", getEstateInfoController);

module.exports = router;

const express = require("express");
const authMiddleware = require("../middleware/authMiddleware");
const adminMiddleware = require("../middleware/adminMiddleware");
const {
  createEstateController,
  getEstateController,
  getEstateInfoController,
  getEstatesFromUser,
  approveEstate,
  getNoApprovedEstates,
  assignSellerController,
} = require("../controllers/estateController");

const router = express.Router();

router.post("/createEstate", authMiddleware, createEstateController);

router.get("/getEstates", getEstateController);

router.get("/getEstateInfo/:id", getEstateInfoController);

router.post("/getEstatesFromUser/:id", authMiddleware, getEstatesFromUser);

router.post("/approveEstate/:id", authMiddleware, approveEstate);

router.get("/getNoApprovedEstates", authMiddleware, getNoApprovedEstates);

router.put("/assignSeller", authMiddleware, assignSellerController);

module.exports = router;

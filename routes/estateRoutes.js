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
  getEstatesApproved,
} = require("../controllers/estateController");

const fields = [
  { name: "presentationImg", maxCount: 1 },
  { name: "images", maxCount: 60 },
];

const upload = require("../config/multerconfig");

const router = express.Router();

router.post(
  "/createEstate",
  upload.fields(fields),
  authMiddleware,
  createEstateController
);

router.get("/getEstates", getEstateController);

router.get("/getEstateInfo/:id", getEstateInfoController);

router.post("/getEstatesFromUser/:id", authMiddleware, getEstatesFromUser);

router.post("/approveEstate/:id", authMiddleware, approveEstate);

router.get("/getNoApprovedEstates", authMiddleware, getNoApprovedEstates);

router.put("/assignSeller", authMiddleware, assignSellerController);

router.get("/getEstatesApproved", getEstatesApproved);

module.exports = router;

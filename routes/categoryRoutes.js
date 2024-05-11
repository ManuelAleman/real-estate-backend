const express = require("express");
const authMiddleware = require("../middleware/authMiddleware");
const {
  addCategoryController,
  getAllCategoryController,
} = require("../controllers/categoryControllers");
const adminMiddleware = require("../middleware/adminMiddleware");

const router = express.Router();

router.post(
  "/addCategory",
  authMiddleware,
  adminMiddleware,
  addCategoryController
);

router.get("/getAllCategory", authMiddleware, getAllCategoryController);
module.exports = router;

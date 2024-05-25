const express = require("express");
const {
  getUserController,
  updateUserController,
  updatePasswordController,
  resetPasswordController,
  deleteProfileController,
  setVerifiedSellerController,
  getVerifiedSellersController,
  getUserInfoFromSeller,
  getAllUsers,
} = require("../controllers/userControllers");
const authMiddleware = require("../middleware/authMiddleware");
const { get } = require("mongoose");

const router = express.Router();

//getDataUser / GET
router.get("/getUser", authMiddleware, getUserController);

//UPDATE PROFILE / PUT
router.put("/updateUser", authMiddleware, updateUserController);

//PASSWORD UPDATE / POST
router.post("/updatePassword", authMiddleware, updatePasswordController);

//RESET PASSWORD / POST
router.post("/resetPassword", authMiddleware, resetPasswordController);

//DELETE USER / DELETE
router.delete("/deleteUser/:id", authMiddleware, deleteProfileController);

//(ADMIN) VERIFY SELLER / PUT
router.put("/setVerifiedSeller", authMiddleware, setVerifiedSellerController);

//GET VERIFIED SELLERS / GET
router.get("/getVerifiedSellers", getVerifiedSellersController);

//GET USER INFO FROM SELLER / POST
router.get("/getUserInfoFromSeller", getUserInfoFromSeller);

router.get("/getAllUsers", authMiddleware, getAllUsers);

module.exports = router;

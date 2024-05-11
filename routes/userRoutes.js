const express = require("express");
const {
  getUserController,
  updateUserController,
  updatePasswordController,
  resetPasswordController,
  deleteProfileController,
  setVerifiedSellerController,
} = require("../controllers/userControllers");
const authMiddleware = require("../middleware/authMiddleware");

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

module.exports = router;

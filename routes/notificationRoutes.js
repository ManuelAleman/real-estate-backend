const express = require("express");
const authMiddleware = require("../middleware/authMiddleware");
const {
  sendNotificationController,
} = require("../controllers/notificationController");

const router = express.Router();

router.post("/sendNotification", authMiddleware, sendNotificationController);

module.exports = router;

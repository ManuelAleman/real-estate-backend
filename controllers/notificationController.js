const userModel = require("../models/userModel");
const Notification = require("../models/notificationModel");

const sendNotificationController = async (req, res) => {
  try {
    // Buscar sender y receiver
    const senderId = req.body.sender;
    const receiverId = req.body.receiver;

    const receiver = await userModel.findById(receiverId);
    if (!receiver) {
      return res.status(404).send({
        success: false,
        message: "Receiver not found",
      });
    }

    const notification = new Notification({
      sender: senderId,
      receiver: receiverId,
      message: req.body.message,
    });
    await notification.save();

    receiver.notifications.push(notification._id);
    await receiver.save();

    res.status(200).send({
      success: true,
      message: "Notification Sent",
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error in SEND NOTIFICATION API",
      error,
    });
  }
};

module.exports = { sendNotificationController };

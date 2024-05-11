const estateModel = require("../models/estateModel");
const sellerModel = require("../models/sellerModel");
const userModel = require("../models/userModel");
const meetingModel = require("../models/meetingModel");

//CREATE MEETING REQUEST
const createMeetingController = async (req, res) => {
  try {
    const { estate_id, date } = req.body;

    if (!estate_id || !date) {
      return res.status(500).send({
        success: false,
        message: "Please fill all the fields",
      });
    }

    const user_id = req.body.id;
    const estate = await estateModel.findById(estate_id);
    const seller_id = estate.seller;

    //Check if a meeting request already exists scheduled for the same date
    const meeting = await meetingModel.findOne({ estate: estate_id, date });
    if (meeting) {
      return res.status(500).send({
        success: false,
        message: "A meeting is already scheduled for this date",
      });
    }

    if(user_id == seller_id){
        return res.status(500).send({
            success: false,
            message: "You cannot request a meeting for your own estate",
        });
    }

    const newMeeting = new meetingModel({
      user: user_id,
      estate: estate_id,
      date,
    });

    await newMeeting.save();
    res.status(200).send({
      success: true,
      message: "Meeting Request Created",
      user : user_id,
      estate : estate_id,
      seller : seller_id
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

module.exports = {createMeetingController};
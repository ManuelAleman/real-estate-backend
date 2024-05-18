const mongoose = require("mongoose");

const estateSchema = new mongoose.Schema(
  {
    presentationImg: {
      type: String,
      required: [true, "Presentation image is required"],
    },
    name: {
      type: String,
      required: [true, "Name is required"],
    },
    description: {
      type: String,
      required: [true, "Description is required"],
    },
    price: {
      type: Number,
      required: [true, "Price is required"],
    },
    type: {
      type: String,
      enum: ["venta", "renta"],
      default: "venta",
    },
    category: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Category",
      required: [true, "Category is required"],
    },
    seller: {
      type: mongoose.Schema.Types.ObjectId,
      required: [true, "Seller is required"],
      ref: "Seller",
    },
    city: {
      type: String,
      required: [true, "City is required"],
    },
    address: {
      type: String,
      required: [true, "Address is required"],
    },
    status: {
      type: String,
      required: [true, "Status is required"],
      default: "wating",
    },
    characteristics: {
      type: [String],
      required: [true, "Characteristics are required"],
    },
    images: {
      type: [String],
      required: [true, "Images are required"],
    },
  },
  { timestamps: true }
);

module.exports = mongoose.model("Estate", estateSchema);

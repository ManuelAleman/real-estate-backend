const express = require("express");
const colors = require("colors");
const cors = require("cors");
const morgan = require("morgan");
const dotenv = require("dotenv");
const connectDb = require("./config/db");
const bodyParser = require("body-parser");
const multer = require("multer");
//dot en configuration
dotenv.config();

connectDb();

//rest objects
const app = express();

//middlewares
app.use(cors());
app.use(express.json());
app.use(morgan("dev"));
app.use(bodyParser.urlencoded({ extended: true }));

//file upload

//route

app.use("/api/v1/test", require("./routes/testRouter"));
app.use("/auth", require("./routes/authRoutes"));
app.use("/users", require("./routes/userRoutes"));
app.use("/categories", require("./routes/categoryRoutes"));
app.use("/estates", require("./routes/estateRoutes"));
app.use("/meetings", require("./routes/meetingRoutes"));
app.use("/notifications", require("./routes/notificationRoutes"));
app.use("/sellers", require("./routes/sellerRoutes"));

app.get("/", (req, res) => {
  return res
    .status(200)
    .send("<h1>WELCOME TO INMOBILIARIAS GODOY API BASE PROJECT </h1>");
});

//port
const PORT = process.env.PORT || 3000;

//listen
app.listen(PORT, () => {
  console.log(`Node Server is running on ${PORT}`.bgMagenta.white);
});

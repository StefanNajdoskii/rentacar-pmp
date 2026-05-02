import { setGlobalOptions } from "firebase-functions";
import { onCall, HttpsError } from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";
import Stripe from "stripe";

setGlobalOptions({ maxInstances: 10 });

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY ?? "", {
  apiVersion: "2023-10-16",
});

export const createPaymentIntent = onCall(async (request) => {
  const { amount, currency, bookingId } = request.data as {
    amount: number;
    currency: string;
    bookingId: string;
  };

  if (typeof amount !== "number" || amount <= 0) {
    throw new HttpsError("invalid-argument", "Amount must be a positive number");
  }
  if (!currency || typeof currency !== "string") {
    throw new HttpsError("invalid-argument", "Currency is required");
  }
  if (!bookingId || typeof bookingId !== "string") {
    throw new HttpsError("invalid-argument", "Booking ID is required");
  }

  logger.info("Creating payment intent", { amount, currency, bookingId });

  try {
    const paymentIntent = await stripe.paymentIntents.create({
      amount,
      currency,
      payment_method_types: ["card"],
      metadata: { bookingId },
    });

    return { clientSecret: paymentIntent.client_secret };
  } catch (error) {
    logger.error("Stripe error creating payment intent", error);
    throw new HttpsError("internal", "Failed to create payment intent");
  }
});

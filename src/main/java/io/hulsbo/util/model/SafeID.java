package io.hulsbo.util.model;

import io.hulsbo.util.model.SafeID;

import static java.util.UUID.randomUUID;

public class SafeID {
    private final String id;

    // Constructor to generate a new safe ID
    private SafeID() {
        this.id = generateSafeId();
    }

    // Private constructor to create a new SafeID instance with a given ID
    private SafeID(String id) {
        this.id = id;
    }

    // Static factory method to generate a new SafeID from a String
    public static SafeID fromString(String id) {
        checkIDValidity(id);
        return new SafeID(id);
    }

    // Static factory method to generate a new SafeID
    public static SafeID randomSafeID() {
        return new SafeID();
    }

    // Method to generate a safe ID
    private static String generateSafeId() {
        String prefix = "id_"; // Ensures the ID starts with a letter
        String safeIdCandidate = prefix + randomUUID().toString().replace("-", "_");
        try {
            checkIDValidity(safeIdCandidate);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Retrying generateSafeId(): ");
            return generateSafeId();
        }
        return safeIdCandidate;
    }

    /**
     * Method to validate ID, throws <code>IllegalArgumentException</code> if not valid.
     */
    private static void checkIDValidity(String id) {
        if (!id.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            throw new IllegalArgumentException("Invalid ID format");
        }
    }

    // Override toString to return the ID value
    @Override
    public String toString() {
        return id;
    }

    // Override equals to compare SafeID objects based on the ID value
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SafeID safeID = (SafeID) obj;
        return id.equals(safeID.id);
    }

    // Override hashCode to return hash based on the ID value
    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
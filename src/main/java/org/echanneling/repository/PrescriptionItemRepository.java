package org.echanneling.repository;

import org.echanneling.model.PrescriptionItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class PrescriptionItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public PrescriptionItemRepository(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // CREATE - Save Prescription Item
    // ==========================================
    public int save(PrescriptionItem item) {

        String sql = """
                INSERT INTO Prescription_Items
                (
                    prescription_id,
                    medicine_name,
                    dosage,
                    frequency,
                    duration,
                    instructions
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                item.getPrescriptionId(),
                item.getMedicineName(),
                item.getDosage(),
                item.getFrequency(),
                item.getDuration(),
                item.getInstructions()
        );
    }


    // ==========================================
    // READ - Get All Items By Prescription
    // ==========================================
    public ArrayList<PrescriptionItem>
    findByPrescriptionId(int prescriptionId) {

        String sql = """
                SELECT
                    prescription_item_id,
                    prescription_id,
                    medicine_name,
                    dosage,
                    frequency,
                    duration,
                    instructions
                FROM Prescription_Items
                WHERE prescription_id = ?
                ORDER BY prescription_item_id
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> mapItem(rs),
                        prescriptionId
                )
        );
    }


    // ==========================================
    // READ - Get Item By ID
    // ==========================================
    public PrescriptionItem findById(
            int prescriptionItemId) {

        String sql = """
                SELECT
                    prescription_item_id,
                    prescription_id,
                    medicine_name,
                    dosage,
                    frequency,
                    duration,
                    instructions
                FROM Prescription_Items
                WHERE prescription_item_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> mapItem(rs),
                prescriptionItemId
        );
    }


    // ==========================================
    // UPDATE - Prescription Item
    // ==========================================
    public int update(
            int prescriptionItemId,
            PrescriptionItem item) {

        String sql = """
                UPDATE Prescription_Items
                SET
                    medicine_name = ?,
                    dosage = ?,
                    frequency = ?,
                    duration = ?,
                    instructions = ?
                WHERE prescription_item_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                item.getMedicineName(),
                item.getDosage(),
                item.getFrequency(),
                item.getDuration(),
                item.getInstructions(),
                prescriptionItemId
        );
    }


    // ==========================================
    // DELETE - Prescription Item
    // ==========================================
    public int delete(int prescriptionItemId) {

        String sql = """
                DELETE FROM Prescription_Items
                WHERE prescription_item_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                prescriptionItemId
        );
    }


    // ==========================================
    // DELETE ALL ITEMS OF PRESCRIPTION
    // ==========================================
    public int deleteByPrescriptionId(
            int prescriptionId) {

        String sql = """
                DELETE FROM Prescription_Items
                WHERE prescription_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                prescriptionId
        );
    }


    // ==========================================
    // MAP SQL RESULT -> JAVA OBJECT
    // ==========================================
    private PrescriptionItem mapItem(
            java.sql.ResultSet rs)
            throws java.sql.SQLException {

        PrescriptionItem item =
                new PrescriptionItem();

        item.setPrescriptionItemId(
                rs.getInt("prescription_item_id")
        );

        item.setPrescriptionId(
                rs.getInt("prescription_id")
        );

        item.setMedicineName(
                rs.getString("medicine_name")
        );

        item.setDosage(
                rs.getString("dosage")
        );

        item.setFrequency(
                rs.getString("frequency")
        );

        item.setDuration(
                rs.getString("duration")
        );

        item.setInstructions(
                rs.getString("instructions")
        );

        return item;
    }
}
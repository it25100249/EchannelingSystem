-- Run once in EChannelingDB before using System Feedback.
-- Reviews are now about the MediConnect system, not individual doctors.

ALTER TABLE Doctor_Reviews ALTER COLUMN doctor_id INT NULL;

-- Existing rows may keep their old doctor_id for historical compatibility.
-- New system-feedback rows are inserted with doctor_id = NULL.

package org.echanneling.designpattern.singleton;

import org.echanneling.model.DoctorSchedule;

/** Classic Singleton used by schedule management for shared schedule validation rules. */
public final class SchedulePolicy {
    private static final SchedulePolicy INSTANCE = new SchedulePolicy();
    private SchedulePolicy() {}
    public static SchedulePolicy getInstance() { return INSTANCE; }

    public void validate(DoctorSchedule schedule) {
        if (schedule.getScheduleDate() == null) throw new IllegalArgumentException("Schedule date is required");
        if (schedule.getStartTime() == null || schedule.getEndTime() == null)
            throw new IllegalArgumentException("Start time and end time are required");
        if (!schedule.getEndTime().isAfter(schedule.getStartTime()))
            throw new IllegalArgumentException("End time must be after start time");
        if (schedule.getMaxPatients() <= 0) throw new IllegalArgumentException("Maximum patients must be greater than 0");
    }
}

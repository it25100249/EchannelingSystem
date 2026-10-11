package org.echanneling.designpattern.decorator;
import org.echanneling.model.PrescriptionItem;
public class MedicineItemDecorator extends PrescriptionDecorator {
    private final PrescriptionItem item;
    public MedicineItemDecorator(PrescriptionComponent wrapped, PrescriptionItem item) { super(wrapped); this.item = item; }
    @Override public String describe() {
        return wrapped.describe() + " | Medicine: " + item.getMedicineName() + ", " + item.getDosage();
    }
}

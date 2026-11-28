public class ProcessData {
    String id;
    int at, bt, remBt, ct, tat, wt, rt, rowIndex;
    boolean firstTime = true;

    public ProcessData(String id, int at, int bt, int rowIndex) {
        this.id = (!id.startsWith("P")) ? "P" + id : id;
        this.at = at;
        this.bt = bt;
        this.remBt = bt;
        this.rowIndex = rowIndex;
    }
}
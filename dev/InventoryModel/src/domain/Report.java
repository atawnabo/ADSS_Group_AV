import java.time.LocalDate;
import java.util.Objects;

public abstract class Report {
    protected int id;
    protected LocalDate date;

    public Report(int id, LocalDate date) {
        if (id < 0) {
            throw new IllegalArgumentException("ID must be non-negative");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        this.id = id;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public abstract String getReportType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;
        Report report = (Report) o;
        return id == report.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
package superli.domain;

public class Availability {
        private int id;
        private int employeeID;
        private int day; 
        private boolean morningShift;
        private boolean eveningShift;
   

        public Availability( int employeeID, int day , boolean morningShift, boolean eveningShift) {
            this.employeeID = employeeID;
            this.day = day; 
            this.morningShift = morningShift;
            this.eveningShift = eveningShift;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public int getEmployeeID() {
            return employeeID;
        }
        public void setEmployeeID(int employeeID) {
            this.employeeID = employeeID;
        }
        public int getDay() {
            return day;
        }
        public void setDay(Integer day) {
            this.day = day;
        }

        public boolean isMorningShift() {
            return morningShift;
        }
        public void setMorningShift(boolean morningShift) {
            this.morningShift = morningShift;
        }
        public boolean isEveningShift() {
            return eveningShift;
        }
        public void setEveningShift(boolean eveningShift) {
            this.eveningShift = eveningShift;
        }


}


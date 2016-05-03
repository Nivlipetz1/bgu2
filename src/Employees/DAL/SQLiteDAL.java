package Employees.DAL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import Employees.BackEnd.*;
import org.sqlite.SQLiteDataSource;

public class SQLiteDAL implements IDAL{

    private SQLiteDataSource dataSource;
    private Connection db;
    private Statement stat;
    private static DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    private static DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private String[][] days = new String[2][7];

    public SQLiteDAL(){
        dataSource = new SQLiteDataSource();
        connected();
        try {
            db = dataSource.getConnection();

        }
        catch (Exception e){
            System.out.println(e);
        }

                /*initialize days strings*/
        days[0][0]="SundayM"; days[1][0]="SundayE";
        days[0][1]="MondayM"; days[1][1]="MondayE";
        days[0][2]="TuesdayM"; days[1][2]="TuesdayE";
        days[0][3]="WednesdayM"; days[1][3]="WednesdayE";
        days[0][4]="ThursdayM"; days[1][4]="ThursdayE";
        days[0][5]="FridayM"; days[1][5]="FridayE";
        days[0][6]="SaturdayM"; days[1][6]="SaturdayE";
    }
    /*
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////// ROLE FUNCTIONS
*/

    public boolean addRole(int roleID, int empID){
        try{
            String sql = "INSERT INTO RolesOfEmployees " +
                    "VALUES ("+roleID+","+empID+")";
            stat = db.createStatement();
            int rows = stat.executeUpdate(sql);
            stat.close();
            return rows==1;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    @Override
    public boolean insertRole(Role role) {
        try{
            String sql = "INSERT INTO Roles " +
                    "VALUES (?,?,?)";
            PreparedStatement preStat = db.prepareStatement(sql);
            preStat.setInt(1,role.getID());
            preStat.setString(2,role.getName());
            preStat.setInt(3,0);
            int rows = preStat.executeUpdate();
            preStat.close();
            return rows==1;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    @Override
    public boolean updateRole(Role role, String name) {
        try{
            String sql = "UPDATE Roles " +
                    "SET Name='"+role.getName()+"' "+
                    "WHERE ID="+role.getID();
            stat = db.createStatement();
            int rows = stat.executeUpdate(sql);
            stat.close();
            return rows==1;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    @Override
    public boolean deleteRole(Role role) {
        try{
            String sql = "UPDATE Roles " +
                    "SET Deleted=1 " +
                    "WHERE ID="+role.getID();
            stat = db.createStatement();
            int rows = stat.executeUpdate(sql);
            stat.close();
            return rows==1;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    /*
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////// END OF ROLE FUNCTIONS
*/



    /*
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////// DAY FUNCTIONS
*/
    @Override
    public Day getDay(LocalDate d) {
        ResultSet set = getListByID("Days","Date","'"+d.format(formatterDate)+"'");
        try{
            int morningShiftID = set.getInt("MorningShift");
            int eveningShiftID = set.getInt("EveningShift");
            set.close();
            stat.close();
            Shift morningShift = getShift(morningShiftID);
            Shift eveningShift = getShift(eveningShiftID);
            return new Day(morningShift,eveningShift,d);
        }
        catch(SQLException e){
            //System.out.println(e);
            return null;
        }

    }

    @Override
    public boolean insert(Day day) {
        try{
            String sql = "INSERT INTO Days VALUES (?,?,?,?)";
            PreparedStatement preStat = db.prepareStatement(sql);
            preStat.setString(1,day.getDate());
            if(day.getMorningShift()!=null){
                preStat.setInt(2, day.getMorningShift().getID());
            }
            else{
                preStat.setInt(2, -1);
            }

            if(day.getEveningShift()!=null){
                preStat.setInt(3, day.getEveningShift().getID());
            }
            else{
                preStat.setInt(3, -1);
            }

            preStat.setInt(4,0);
            int rows = preStat.executeUpdate();
            preStat.close();
            return rows==1;
        }
        catch (SQLException e){
            return false;
        }
    }

    @Override
    public boolean delete(Day day) {
        try{
            stat = db.createStatement();
            int rows = stat.executeUpdate("UPDATE Days SET Deleted=1 WHERE Date="+day.getDate());
            stat.close();
            return rows==1;
        }
        catch (SQLException e){
            return false;
        }
    }

    @Override
    public boolean update(Day day) {
        try {
            String sql = "UPDATE Days " +
                    "SET MorningShift=?, EveningShift=? " +
                    "WHERE Date='"+day.getDate()+"'";
            PreparedStatement preStat = db.prepareStatement(sql);
            preStat.setInt(1,day.getMorningShift().getID());
            preStat.setInt(2,day.getEveningShift().getID());
            int rows = preStat.executeUpdate();
            preStat.close();
            return rows==1;
        }
        catch (SQLException e){
            return false;
        }
    }

    /**
     * used to get the next shiftID
     * @return the shift id or -1 if there was an error
     */
    public int shiftID(){
        try {
            stat = db.createStatement();
            String sql = "SELECT COUNT(*) FROM Shifts";
            ResultSet set = stat.executeQuery(sql);
            int count = set.getInt(1);
            set.close();
            stat.close();
            return count;
        }catch (SQLException e){
            return -1;
        }
    }

        /*
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////// END OF DAY FUNCTIONS
*/

    /*
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////// SHIFT FUNCTIONS
*/
    @Override
    public Shift getShift(int id) {
        String value = Integer.toString(id);
        ResultSet set = getListByID("Shifts","ID",value);
        try{
            int duration = set.getInt("Duration");
            LocalTime startTime = LocalTime.parse(set.getString("StartTime"),formatterTime);
            LocalTime endTime = LocalTime.parse(set.getString("EndTime"),formatterTime);
            LocalDate date = LocalDate.parse(set.getString("Date"),formatterDate);
            int managerID = set.getInt("ManagerID");
            int deleted = set.getInt("Deleted");
            set.close();
            stat.close();
            if (deleted == 1)
                return null;
            Employee manager = getEmployee(managerID);
            HashMap<Integer,Integer> map = getRolesOfShift(id);
            Vector<Pair> roles = getEmployeesOfShift(id);
            return new Shift(id,startTime,endTime,duration,date,manager,roles, map);
        }
        catch (SQLException e){
            return null;
        }
    }

    private HashMap<Integer,Integer> getRolesOfShift(int id){
        HashMap<Integer,Integer> map = new HashMap<>();
        try {
            ResultSet set = stat.executeQuery("SELECT * FROM RolesOfShifts WHERE ShiftID=" + id);
            while(set.next()){
                map.put(set.getInt("RoleID"),set.getInt("Amount"));
            }
            set.close();
            stat.close();
            return map;
        }
        catch (SQLException e){
            return map;
        }
    }

    private Vector<Pair> getEmployeesOfShift(int id){
        HashMap<Integer,Integer> map = new HashMap<>();
        Vector<Pair> vec = new Vector<>();
        try {
            ResultSet set = stat.executeQuery("SELECT * FROM EmployeesInShifts WHERE ShiftID=" + id);
            while(set.next()){
                map.put(set.getInt("EmployeeID"),set.getInt("RoleID"));
            }
            set.close();
            stat.close();
            Set<Integer> keys = map.keySet();

            for(int key: keys){
                Employee emp = getEmployee(key);
                Role role = getRole(map.get(key));
                Pair pair = new Pair(role,emp);
                if(emp!=null)
                    vec.add(pair);
            }
        }
        catch (SQLException e){

        }
        return vec;
    }

    private void insertRolesOfShifts(Shift shift) throws SQLException{
        String sql = "INSERT INTO RolesOfShifts VALUES (?,?,?)";
        PreparedStatement preStat = db.prepareStatement(sql);
        for(Pair p: shift.getRoles()){
            if(!(roleExists(shift.getID(),p.getRole(),"RolesOfShifts","ShiftID"))){
                preStat.setInt(1,p.getRole().getID());
                preStat.setInt(2,shift.getID());
                preStat.setInt(3,shift.getAmountOfRoles().get(p.getRole().getID()));
                preStat.executeUpdate();
                preStat.clearParameters();
            }
        }
        preStat.close();
    }
    private void insertEmployeesOfShifts(Shift shift) throws SQLException{
        String sql = "INSERT INTO EmployeesInShifts VALUES (?,?,?)";
        PreparedStatement preStat = db.prepareStatement(sql);
        for(Pair p: shift.getRoles()){
            if(p.getEmployee()!=null){
                preStat.setInt(2,shift.getID());
                preStat.setInt(1,p.getEmployee().getId());
                preStat.setInt(3,p.getRole().getID());
                preStat.executeUpdate();
                preStat.clearParameters();
            }
        }
        preStat.close();
    }
    @Override
    public boolean insert(Shift shift) {
        try {
            String sql = "INSERT INTO Shifts VALUES (?,?,?,?,?,?,?)";
            PreparedStatement preStat = db.prepareStatement(sql);
            preStat.setInt(1, shift.getID());
            preStat.setString(2, shift.getStartTime());
            preStat.setString(3, shift.getEndTime());
            preStat.setInt(4, shift.getDuration());
            preStat.setString(5, shift.getDate());
            preStat.setInt(6, shift.getManager().getId());
            preStat.setInt(7, 0);
            preStat.executeUpdate();
            preStat.close();
            insertRolesOfShifts(shift);
            insertEmployeesOfShifts(shift);
            return true;
        }
        catch (SQLException e){
            return false;
        }
    }

    @Override
    public boolean delete(Shift shift) {
        String sql = "UPDATE Shifts\n" +
                "SET Deleted = 1\n"+
                "WHERE ID = "+shift.getID()+";";
        try {
            Statement stat = db.createStatement();
            int rows = stat.executeUpdate(sql);
            return rows>0;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean update(Shift shift) {
        String sql = "UPDATE Shifts " +
                "SET Date=? , Duration=? , EndTime= ? , StartTime=?, ManagerID=?" +
                "WHERE ID=?";
        try {
            PreparedStatement preStat = db.prepareStatement(sql);
            preStat.setInt(6, shift.getID());
            preStat.setString(4, shift.getStartTime());
            preStat.setString(3, shift.getEndTime());
            preStat.setInt(2, shift.getDuration());
            preStat.setString(1, shift.getDate());
            preStat.setInt(5, shift.getManager().getId());
            preStat.executeUpdate();
            stat = db.createStatement();
            stat.executeUpdate("DELETE FROM RolesOfShifts" +
                    " WHERE ShiftID="+shift.getID());
            stat.close();
            insertRolesOfShifts(shift);
            stat = db.createStatement();
            stat.executeUpdate("DELETE FROM EmployeesInShifts " +
                    "WHERE ShiftID="+shift.getID());
            stat.close();
            insertEmployeesOfShifts(shift);
            return true;
        }
        catch(SQLException e){
            return false;
        }
    }


        /*
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////// END OF SHIFT FUNCTIONS
*/


    /*
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////// EMPLOYEE FUNCTIONS
*/
    @Override
    public Employee getEmployee(int id) {
        try{
            ResultSet set = getListByID("Employees","ID",Integer.toString(id));
            String firstName = set.getString("FirstName");
            String lastName = set.getString("LastName");
            String contract = set.getString("Contract");
            LocalDate dateOfHire = LocalDate.parse(set.getString("DateOfHire"),formatterDate);
            String bankAccount = set.getString("BankAccount");
            int deleted = set.getInt("Deleted");
            set.close();
            stat.close();
            if (deleted==1)
                return null;
            Vector<Role> roles = getEmployeeRoles(id);
            int[][] availability = getAvailability(id);
            Employee emp = new Employee(firstName,lastName,
                    id,roles,dateOfHire,contract,bankAccount,availability);
            return emp;
        }
        catch (SQLException e){

        }
        return null;
    }

    private int[][] getAvailability(int id){
        int[][] array = new int[2][7];
        try {
            ResultSet set = getListByID("EmployeeAvailability", "EmployeeID", Integer.toString(id));
            set.next();
            for(int i=0;i<7;i++){
                for(int j=0;j<2;j++){
                    array[j][i] = set.getInt(days[j][i]);
                }
            }
            set.close();
            stat.close();
        }
        catch (SQLException e){

        }
        return array;
    }

    private Vector<Role> getEmployeeRoles(int id){
        Vector<Role> vec = new Vector<>();
        Vector<Integer> roleIDs = new Vector<>();
        try {
            ResultSet set = stat.executeQuery("SELECT * FROM RolesOfEmployees WHERE EmployeeID=" + id);
            while (set.next()) {
                roleIDs.add(set.getInt("RoleID"));
            }
            set.close();
            stat.close();
            for(Integer roleID: roleIDs){
                Role role = getRole(roleID);
                if (role!=null)
                    vec.add(role);
            }
        }
        catch (SQLException e){

        }
        return vec;
    }

    @Override
    public Role getRole(int id) {
        try{
            Role role = null;
            ResultSet set = getListByID("Roles","ID",Integer.toString(id));
            if (set.getInt("Deleted")==0)
                role = new Role(set.getInt("ID"),set.getString("Name"));
            set.close();
            stat.close();
            return role;
        }
        catch (SQLException e){
            return null;
        }
    }


    @Override
    public boolean insert(Employee emp) {
        try {
            //insert new emp
            String sql = "INSERT INTO Employees VALUES (?,?,?,?,?,?,?)";
            PreparedStatement preStat = db.prepareStatement(sql);
            preStat.setInt(1,emp.getId());
            preStat.setString(2,emp.getFirstName());
            preStat.setString(3,emp.getLastName());
            preStat.setString(4,emp.getContract());
            preStat.setString(5,emp.getDateOfHire().toString());
            preStat.setString(6,emp.getBankAcct());
            preStat.setInt(7,0);
            preStat.executeUpdate();
            preStat.close();
            for(Role r: emp.getRoles()){
                addRole(r.getID(), emp.getId());
            }
            employeeAvailability(emp);
        }
        catch(Exception e){
            System.out.print("insert employee");
            System.out.print(e);
            return false;
        }
        return true;
    }

    private boolean employeeAvailability(Employee emp){
        try {
            String sql = "INSERT INTO EmployeeAvailability VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ;";
            PreparedStatement preparedStatement =
                    db.prepareStatement(sql);
            preparedStatement.setInt(1, emp.getId());
            int counter=2;
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 2; j++) {
                    preparedStatement.setInt(counter, emp.getAvailability()[j][i]);
                    counter++;
                }
            }
            preparedStatement.setInt(16,0);//Deleted attribute
            int rows = preparedStatement.executeUpdate();
            preparedStatement.close();
            return rows==1;
        }
        catch (SQLException e){
            System.out.println(e);
            return false;
        }
    }

    @Override
    public boolean delete(Employee emp) {
        String sql = "UPDATE Employees\n" +
                    "SET Deleted = 1\n"+
                    "WHERE ID = "+emp.getId()+";";
        try {
            stat = db.createStatement();
            int rows = stat.executeUpdate(sql);
            stat.close();
            return rows>0;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean update(Employee emp) {
        String sql = "UPDATE Employees " +
                "SET FirstName=? , LastName=? , Contract = ? , DateOfHire = ? , BankAccount = ?" +
                "WHERE ID=?";
        try{
            PreparedStatement preparedStatement =
                    db.prepareStatement(sql);

            preparedStatement.setString(1, emp.getFirstName());
            preparedStatement.setString(2, emp.getLastName());
            preparedStatement.setString(3, emp.getContract());
            preparedStatement.setString(4, emp.getDateOfHire().toString());
            preparedStatement.setString(5, emp.getBankAcct());
            preparedStatement.setInt(6, emp.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();
            stat = db.createStatement();
            stat.executeUpdate("DELETE FROM EmployeeAvailability WHERE EmployeeID = "+emp.getId());
            stat.close();
            employeeAvailability(emp);
            stat = db.createStatement();
            stat.executeUpdate("DELETE FROM RolesOfEmployees WHERE EmployeeID = "+emp.getId());
            stat.close();
            for(Role role: emp.getRoles()){
                if (!roleExists(emp.getId(),role,"rolesOfEmployees","EmployeeID")){
                    addRole(role.getID(),emp.getId());
                }
            }
            return (rowsAffected==1);

        } catch(SQLException e){
            System.out.println(e);
            return false;
        }

    }


        /*
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////// END OF EMPLOYEE FUNCTIONS
*/

    private boolean roleExists(int ID,Role role,String table,String attribute) throws SQLException{

        String sql = "SELECT COUNT(*) FROM "+table+
                " WHERE "+attribute+"=? AND RoleID=?";
        PreparedStatement preStat = db.prepareStatement(sql);
        preStat.setInt(1,ID);
        preStat.setInt(2,role.getID());
        ResultSet set = preStat.executeQuery();
        int count = set.getInt(1);
        set.close();
        preStat.close();
        return count>0;
    }



    /**
     * used to get the next RoleID
     * @return the Role id or -1 if there was an error
     */
    public int roleID(){
        try {
            stat = db.createStatement();
            String sql = "SELECT COUNT(*) FROM Roles";
            ResultSet set = stat.executeQuery(sql);
            int count = set.getInt(1);
            set.close();
            stat.close();
            return count+1;
        }catch (SQLException e){
            System.out.println(e);
            return -1;
        }
    }

    private boolean connected() {
        dataSource.setUrl("jdbc:sqlite:superlee");
        return true;

    }
    public Vector<Role> getRoles(){
        Vector<Role> list = new Vector<Role>();
        try {

            ResultSet set = getListByID("Roles", null, null);
            while(set.next()){
                if(set.getInt("Deleted")==0) {
                    Role role = new Role(set.getInt(1), set.getString(2));
                    //System.out.println(role.getID()+" "+role.getName());
                    list.add(role);
                }
            }
            set.close();
            stat.close();

        }
        catch (SQLException e){
            System.out.println(e);
        }
        return list;
    }

    @Override
    public Vector<Employee> getEmployees() {
        Vector<Employee> employees = new Vector<>();
        Vector<Integer> ids = new Vector<>();
        try{
            ResultSet set = getListByID("Employees",null,null);
            while(set.next()){
                ids.add(set.getInt("ID"));
            }
            set.close();
            stat.close();
            for(Integer id : ids){
                employees.add(getEmployee(id));
            }

        } catch (SQLException e){

        }
        return employees;

    }

    @Override
    public boolean idExists(int id) {
        Employee emp = getEmployee(id);
        return emp!=null;
    }

    private ResultSet getListByID(String table,String attribute,String value){
        String sql = "SELECT * FROM "+table+" " +
                "WHERE Deleted=0";
        if (attribute!=null)
                    sql += " AND "+attribute+"="+value;
        try {
            stat = db.createStatement();
            ResultSet set = stat.executeQuery(sql);
            return set;
        }
        catch (SQLException e){
            System.out.println(e);
            return null;
        }
    }

    public Vector<Employee> getAvailableEmployees(int[][] avail){
        Vector<Employee> employees = new Vector<>();

        String attribute = days[avail[0][0]][avail[0][1]];
        try {
            stat = db.createStatement();
            ResultSet set = stat.executeQuery("SELECT EmployeeID FROM EmployeeAvailability " +
                    "WHERE "+attribute+"=1");
            Vector<Integer> ids = new Vector<>();
            while (set.next()){
                ids.add(set.getInt("EmployeeID"));
            }
            set.close();
            stat.close();
            for(Integer id : ids){
                Employee emp = getEmployee(id);
                if (emp!=null)
                    employees.add(emp);
            }
        }
        catch (SQLException e){

        }
        return employees;
    }

    @Override
    public Shift getShift(LocalDate d, LocalTime startTime) {
        try{
            stat = db.createStatement();
            ResultSet set = stat.executeQuery("SELECT ID FROM Shifts " +
                    "WHERE Date='"+d.format(formatterDate)+"' AND StartTime='"+startTime.format(formatterTime)+"'");
            int id = set.getInt("ID");
            set.close();
            stat.close();
            Shift shift = getShift(id);
            return shift;
        }
        catch (SQLException e){
            return null;
        }
    }
}

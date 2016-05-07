package Employees.DAL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import Employees.BackEnd.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by omer on 01/05/16.
 */
public class SQLiteDALTest {
    private IDAL sqliteDal;
    private static DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    @Before
    public void setUp() throws Exception {
        sqliteDal = new SQLiteDAL();
    }

    @After
    public void tearDown() throws Exception{
        sqliteDal.deleteRole(new Role(555,"TEST ROLE"));
        sqliteDal.deleteRole(new Role(666,"TEST ROLE"));
        sqliteDal.delete(new Employee("FirstName","LastName",555,null,LocalDate.now(),"CONTRACT","BANKACC",null));
        sqliteDal.delete(new Employee("FirstName","LastName",666,null,LocalDate.now(),"CONTRACT","BANKACC",null));
        sqliteDal.delete(new Employee("FirstName","LastName",777,null,LocalDate.now(),"CONTRACT","BANKACC",null));
    }

    @Test
    public void insertRole() throws Exception {
        Role role = new Role(555,"TEST ROLE");
        sqliteDal.insertRole(role);
        assertEquals(sqliteDal.getRole(555).getName(),"TEST ROLE");
    }

    @Test
    public void insert() throws Exception {
        Employee emp = new Employee("FirstName","LastName",555,null,LocalDate.now(),"CONTRACT","BANKACC",null);
        sqliteDal.insert(emp);
        assertEquals(emp.getFirstName(),sqliteDal.getEmployee(555).getFirstName());
    }

    @Test
    public void delete() throws Exception {
        Employee emp = new Employee("FirstName","LastName",777,null,LocalDate.now(),"CONTRACT","BANKACC",null);
        sqliteDal.insert(emp);
        assertTrue(sqliteDal.delete(new Employee("FirstName","LastName",777,null,LocalDate.now(),"CONTRACT","BANKACC",null)));
    }

    @Test
    public void insertDay() throws Exception{
        Day day = new Day(null,null,LocalDate.parse("01/01/2000",formatterDate));
        sqliteDal.insert(day);
        assertTrue(sqliteDal.getDay(LocalDate.parse("01/01/2000",formatterDate))!=null);
    }

    @Test
    public void getDay() throws Exception{
        Day day = new Day(null,null,LocalDate.parse("02/01/2000",formatterDate));
        sqliteDal.insert(day);
        assertTrue(sqliteDal.getDay(LocalDate.parse("02/01/2000",formatterDate))!=null);
    }

    @Test
    public void deleteDay() throws Exception{
        Day day = new Day(null,null,LocalDate.parse("03/01/2000",formatterDate));
        sqliteDal.insert(day);
        assertTrue(sqliteDal.delete(new Day(null,null,LocalDate.parse("03/01/2000",formatterDate))));
    }



    @Test
    public void idExists() throws Exception {
        Employee emp = new Employee("FirstName","LastName",666,null,LocalDate.now(),"CONTRACT","BANKACC",null);
        sqliteDal.insert(emp);
        assertTrue(sqliteDal.idExists(666));
    }

    @Test
    public void getRole() throws Exception {
        Role role = new Role(666,"TEST ROLE");
        sqliteDal.insertRole(role);
        assertEquals(sqliteDal.getRole(666).getName(),"TEST ROLE");
    }

    @Test
    public void shiftID() throws Exception{
        assertEquals(1,sqliteDal.shiftID());
    }

    @Test
    public void roleID() throws Exception{
        assertTrue(sqliteDal.roleID()>2);
    }

}
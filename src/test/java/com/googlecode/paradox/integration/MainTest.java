/*
 * MainTest.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.integration;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.utils.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Generic integration tests for Paradox Driver.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
@Category(IntegrationTest.class)
public class MainTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Register the database driver.
     *
     * @throws ClassNotFoundException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws SQLException in case of failures.
     */
    @After
    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws SQLException in case of failures.
     */
    @Before
    public void connect() throws SQLException {
        this.conn = DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for the catalog metadata.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCatalog() throws SQLException {

        final DatabaseMetaData meta = this.conn.getMetaData();
        try (ResultSet rs = meta.getCatalogs()) {
            if (rs.next()) {
                Assert.assertEquals("test-classes", rs.getString("TABLE_CAT"));
            } else {
                Assert.fail("No catalog selected.");
            }
        }
    }

    /**
     * Test for the index info metadata.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testIndexInfo() throws SQLException {
        final String[] names = new String[2];
        final DatabaseMetaData meta = this.conn.getMetaData();

        try (ResultSet rs = meta.getIndexInfo("test-classes", "db", "customer.db", true, true)) {
            Assert.assertTrue(rs.next());
            names[0] = rs.getString("INDEX_NAME");
            Assert.assertTrue(rs.next());
            names[1] = rs.getString("INDEX_NAME");
            Assert.assertTrue(rs.next());

            Arrays.sort(names);
            Assert.assertEquals("CUSTOMER.PX", names[0]);
            Assert.assertEquals("CUSTOMER.X06", names[1]);

            while (rs.next()) {
                Assert.assertEquals("test-classes", rs.getString("TABLE_CAT"));
                Assert.assertEquals("db", rs.getString("TABLE_SCHEM"));
                Assert.assertNull(rs.getString("TABLE_NAME"));
                Assert.assertEquals("false", rs.getString("NON_UNIQUE"));
                Assert.assertEquals("test-classes", rs.getString("INDEX_QUALIFIER"));
                Assert.assertEquals("CUSTOMER.X06", rs.getString("INDEX_NAME"));
                Assert.assertEquals("2", rs.getString("TYPE"));
                Assert.assertEquals("0", rs.getString("ORDINAL_POSITION"));
                Assert.assertEquals("City", rs.getString("COLUMN_NAME"));
                Assert.assertEquals("A", rs.getString("ASC_OR_DESC"));
                Assert.assertEquals("0", rs.getString("CARDINALITY"));
                Assert.assertEquals("0", rs.getString("PAGES"));
                Assert.assertNull(rs.getString("FILTER_CONDITION"));
            }
        }
    }

    /**
     * Test for primary key metadata.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testPrimaryKey() throws SQLException {
        final DatabaseMetaData meta = this.conn.getMetaData();

        try (ResultSet rs = meta.getPrimaryKeys("test-classes", "db", "CUSTOMER.db")) {
            Assert.assertTrue(rs.next());
            Assert.assertEquals("test-classes", rs.getString("TABLE_CAT"));
            Assert.assertEquals("db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("CUSTOMER", rs.getString("TABLE_NAME"));
            Assert.assertEquals("CustNo", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("0", rs.getString("KEY_SEQ"));
            Assert.assertEquals("CustNo", rs.getString("PK_NAME"));
            Assert.assertFalse(rs.next());
        }
    }

    /**
     * Test for {@link ResultSet} execution.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testResultSet() throws SQLException {

        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT AC as 'ACode', State, CITIES FROM AREACODES")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Column 'AC':", "201", rs.getString("ac"));
            Assert.assertEquals("Column 'State':", "NJ", rs.getString("State"));
            Assert.assertEquals("Column 'Cities':", "Hackensack, Jersey City (201/551 overlay)",
                    rs.getString("Cities"));
        }
    }

    /**
     * Test for {@link ResultSet} with multiple values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testResultSetMultipleValues() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT \"id\", name, moneys FROM \"general.db\"")) {
            Assert.assertTrue("First record:", rs.next());
            Assert.assertEquals("1 row: ", "1 - Mari 100.0",
                    rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
            Assert.assertTrue("Second record:", rs.next());
            Assert.assertEquals("2 row: ", "2 - Katty 150.0",
                    rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
            Assert.assertTrue("Third record:", rs.next());
            Assert.assertEquals("2 row: ", "333333333 - Elizabet 75.0",
                    rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
        }
    }

    /**
     * Test {@link ResultSet} with one column.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testResultSetOneColumn() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT email FROM customer")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("1 row:", "luke@fun.com", rs.getString("email"));
            Assert.assertTrue("No second row", rs.next());
            Assert.assertEquals("2 row:", "fmallory@freeport.org", rs.getString("email"));
            Assert.assertTrue("No third row", rs.next());
            Assert.assertEquals("3 row:", "lpetzold@earthenwear.com", rs.getString("email"));
        }
    }

    /**
     * Test {@link ResultSet} with two columns.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testResultSetTwoColumn() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT email,custno  FROM customer")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("1 row:", "luke@fun.com", rs.getString(1));
            Assert.assertEquals("1 row:", 1, rs.getInt(2));
            Assert.assertTrue("No second row", rs.next());
            Assert.assertEquals("2 row:", "fmallory@freeport.org", rs.getString("email"));
            Assert.assertEquals("2 row:", 2, rs.getInt("custNo"));
            Assert.assertTrue("No third row", rs.next());
            Assert.assertEquals("3 row:", "lpetzold@earthenwear.com", rs.getString("Email"));
            Assert.assertEquals("2 row:", 3, rs.getInt("CUSTNO"));
        }
    }

    /**
     * Test for unwrap impossible.
     *
     * @throws SQLException if test succeed.
     */
    @Test(expected = SQLException.class)
    public void testUnwrapImpossible() throws SQLException {
        Utils.unwrap(this.conn, Integer.class);
    }

    /**
     * Test for a valid connection.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testValidConnection() throws SQLException {
        Assert.assertTrue("Wrapper invalid.", this.conn.isWrapperFor(ParadoxConnection.class));
        Assert.assertNotNull("Can't unwrap.", this.conn.unwrap(ParadoxConnection.class));
    }

    /**
     * Test for view columns metadata.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testViewColumns() throws SQLException {
        final DatabaseMetaData meta = this.conn.getMetaData();

        try (ResultSet rs = meta.getColumns("test-classes", "db", "AREAS.QBE", "%")) {
            // Test for AC field.
            Assert.assertTrue(rs.next());
            Assert.assertEquals("Testing for table catalog.", "test-classes", rs.getString("TABLE_CAT"));
            Assert.assertEquals("Testing for table schema.", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Testing for table name.", "AREAS.QBE", rs.getString("TABLE_NAME"));
            Assert.assertEquals("Testing for column name.", "AC", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("Testing for data type.", 12, rs.getInt("DATA_TYPE"));
            Assert.assertEquals("Testing for type name.", "VARCHAR", rs.getString("TYPE_NAME"));
            Assert.assertEquals("Testing for column size.", 5, rs.getInt("COLUMN_SIZE"));
            Assert.assertNull("Testing for nullable.", rs.getString("NULLABLE"));
            Assert.assertEquals("Testing for is nullable.", "YES", rs.getString("IS_NULLABLE"));
            Assert.assertEquals("Testing for is auto increment field.", "NO", rs.getString("IS_AUTOINCREMENT"));

            // Test for State field.
            Assert.assertTrue(rs.next());
            Assert.assertEquals("Testing for table catalog.", "test-classes", rs.getString("TABLE_CAT"));
            Assert.assertEquals("Testing for table schema.", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Testing for table name.", "AREAS.QBE", rs.getString("TABLE_NAME"));
            Assert.assertEquals("Testing for column name.", "State", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("Testing for data type.", 12, rs.getInt("DATA_TYPE"));
            Assert.assertEquals("Testing for type name.", "VARCHAR", rs.getString("TYPE_NAME"));
            Assert.assertEquals("Testing for column size.", 3, rs.getInt("COLUMN_SIZE"));
            Assert.assertNull("Testing for nullable.", rs.getString("NULLABLE"));
            Assert.assertEquals("Testing for is nullable.", "YES", rs.getString("IS_NULLABLE"));
            Assert.assertEquals("Testing for is auto increment field.", "NO", rs.getString("IS_AUTOINCREMENT"));

            // Test for Cities field.
            Assert.assertTrue(rs.next());
            Assert.assertEquals("Testing for table catalog.", "test-classes", rs.getString("TABLE_CAT"));
            Assert.assertEquals("Testing for table schema.", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Testing for table name.", "AREAS.QBE", rs.getString("TABLE_NAME"));
            Assert.assertEquals("Testing for column name.", "Cities", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("Testing for data type.", 12, rs.getInt("DATA_TYPE"));
            Assert.assertEquals("Testing for type name.", "VARCHAR", rs.getString("TYPE_NAME"));
            Assert.assertEquals("Testing for column size.", 157, rs.getInt("COLUMN_SIZE"));
            Assert.assertNull("Testing for nullable.", rs.getString("NULLABLE"));
            Assert.assertEquals("Testing for is nullable.", "YES", rs.getString("IS_NULLABLE"));
            Assert.assertEquals("Testing for is auto increment field.", "NO", rs.getString("IS_AUTOINCREMENT"));

            // No more results.
            Assert.assertFalse(rs.next());
        }
    }
}

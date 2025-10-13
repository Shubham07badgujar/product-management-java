package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import dao.ProductDaoImplMockitoTest;
import dao.ProductDaoImplTest;
import main.AppTest;
import model.ProductValidationTest;
import service.ProductServiceTest;

/**
 * Test Suite for the Product Management System
 * This runs all test classes in the project
 */
@Suite
@SuiteDisplayName("Product Management System Test Suite")
@SelectClasses({
        ProductValidationTest.class,
        ProductDaoImplMockitoTest.class,
        ProductDaoImplTest.class,
        ProductServiceTest.class,
        AppTest.class
})
public class AllTestsSuite {
    // This class remains empty, it is used only as a holder for the above
    // annotations
}
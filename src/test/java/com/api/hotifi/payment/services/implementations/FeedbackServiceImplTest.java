package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.payment.services.interfaces.IFeedbackService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

//Only one instance of feedbackService instance will be created no matter how many instances you have in each method
//Default Annotation - @TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
        // after writing this static method is not required @BeforeAll
class FeedbackServiceImplTest {

    IFeedbackService feedbackService;
    TestInfo testInfo;
    TestReporter testReporter;

    @BeforeAll
    void beforeAllInit(TestInfo testInfo, TestReporter testReporter) {
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("Before All");
        testReporter.publishEntry("Running " + testInfo.getDisplayName() + " with tag "+ testInfo.getTags());
    }

    @AfterAll
    static void afterAllInit() {
        System.out.println("After All");
    }

    @BeforeEach
    void init() {
        System.out.println("Before for each..");
        feedbackService = new FeedbackServiceImpl();
    }

    @AfterEach
    void cleanUp() {
        System.out.println("Clean up...");
    }

    @Nested
    class NestedTest{

        @Test
        void nestedOne(){
            assertEquals(2, 4);
        }

        @Test
        void nestedTwo(){
            assertEquals(2, 3);
        }
    }

    @Test
    @DisplayName("Testing method")
    void test() {
        boolean isServerUp = true;// If false test won't run
        assumeTrue(isServerUp); //expecting test to run, if assumption is false further lines will be not executed
        //Add objects and methods here
        int expected = 1;
        assertEquals(1, 2, () -> "Should add numbers" + expected); // should add numers will be printed only if test fails, else no message will be displayed
        assertArrayEquals(null, new int[20]);
        assertIterableEquals(null, null);
        assertThrows(ArithmeticException.class, () -> feedbackService.addFeedback(null));
        //Life cycle hooks
        // @BeforeAll @BeforeEach @AfterAll @AfterEach
        assertAll(
                () -> assertEquals(7, 4),
                () -> assertEquals(7, 4),
                () -> assertEquals(7, 4),
                () -> assertEquals(7, 4));

    }

    @Test
    @Tag("tag") // configure it in configs to run important method tests in tags
    @RepeatedTest(3) // 3 times test runs
    @Disabled // skips the test
    @DisplayName("Should not run")
    void skipTest(RepetitionInfo repetitionInfo) {
        if(repetitionInfo.getCurrentRepetition() == 1)
            System.out.println("1st repetition");
        System.out.println("Skipped");
    }


}
package alzara.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import alzara.AlzaraException;
import alzara.command.AddCommand;
import alzara.command.Command;
import alzara.command.DeleteCommand;
import alzara.command.ExitCommand;
import alzara.command.FindCommand;
import alzara.command.ListCommand;
import alzara.command.MarkCommand;
import alzara.command.UnmarkCommand;

/**
 * Tests for {@link CommandParser#parse(String)}.
 *
 * Scope note: AddCommand/MarkCommand/UnmarkCommand/DeleteCommand expose no getters for
 * their parsed fields, and their execute() calls the static Storage.save(...), which
 * would write to the real data/alzara.txt if run. So the "valid input" tests below only
 * assert the returned Command's type (assertInstanceOf) and that no exception is thrown -
 * they deliberately never call execute(). The "invalid input" tests are the more valuable
 * half: they assert the specific AlzaraException and its message, which is fully checkable
 * through the public API alone.
 */
class CommandParserTest {

    // --- bye ---

    // parse("bye") should return an ExitCommand, no exception thrown.
    @Test
    void parse_byeCommand_returnsExitCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("bye"));
        assertInstanceOf(ExitCommand.class, result);
    }

    // CommandType.from uses equalsIgnoreCase for "bye" - parse("BYE") should also
    // return an ExitCommand.
    @Test
    void parse_byeCommandDifferentCase_returnsExitCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("BYE"));
        assertInstanceOf(ExitCommand.class, result);
    }

    // --- list ---

    // parse("list") should return a ListCommand, no exception thrown.
    @Test
    void parse_listCommand_returnsListCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("list"));
        assertInstanceOf(ListCommand.class, result);
    }

    // --- mark ---

    // parse("mark 1") should return a MarkCommand, no exception thrown.
    @Test
    void parse_markValidIndex_returnsMarkCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, result);
    }

    // parse("mark") (no index token at all) should throw AlzaraException with
    // message AlzaraException.MISSING_TASK_NUMBER_MESSAGE.
    @Test
    void parse_markMissingIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("mark"));
        assertEquals(AlzaraException.MISSING_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // parse("mark abc") (index token isn't a number) should throw AlzaraException
    // with message AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE.
    @Test
    void parse_markNonNumericIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("mark abc"));
        assertEquals(AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // --- unmark ---

    // parse("unmark 1") should return an UnmarkCommand, no exception thrown.
    @Test
    void parse_unmarkValidIndex_returnsUnmarkCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("unmark 1"));
        assertInstanceOf(UnmarkCommand.class, result);
    }

    // parse("unmark") should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_NUMBER_MESSAGE.
    @Test
    void parse_unmarkMissingIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("unmark"));
        assertEquals(AlzaraException.MISSING_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // parse("unmark abc") should throw AlzaraException with message
    // AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE.
    @Test
    void parse_unmarkNonNumericIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("unmark abc"));
        assertEquals(AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // --- delete ---

    // parse("delete 1") should return a DeleteCommand, no exception thrown.
    @Test
    void parse_deleteValidIndex_returnsDeleteCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("delete 1"));
        assertInstanceOf(DeleteCommand.class, result);
    }

    // parse("delete") should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_NUMBER_MESSAGE.
    @Test
    void parse_deleteMissingIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("delete"));
        assertEquals(AlzaraException.MISSING_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // parse("delete abc") should throw AlzaraException with message
    // AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE.
    @Test
    void parse_deleteNonNumericIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("delete abc"));
        assertEquals(AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // --- todo ---

    // parse("todo read book") should return an AddCommand, no exception thrown.
    @Test
    void parse_todoWithDescription_returnsAddCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, result);
    }

    // parse("todo") (bare command, no description at all) should throw
    // AlzaraException with message AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_todoMissingDescription_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("todo"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // --- deadline ---

    // parse("deadline return book /by 2019-10-15") should return an AddCommand,
    // no exception thrown.
    @Test
    void parse_deadlineWithDescriptionAndDate_returnsAddCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("deadline return book /by 2019-10-15"));
        assertInstanceOf(AddCommand.class, result);
    }

    // parse("deadline") (bare command) should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_deadlineMissingDescription_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("deadline"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // parse("deadline return book") (no " /by " marker at all) should throw
    // AlzaraException with message AlzaraException.MISSING_DEADLINE_MARKER_MESSAGE.
    @Test
    void parse_deadlineMissingByMarker_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class,
                () -> CommandParser.parse("deadline return book"));
        assertEquals(AlzaraException.MISSING_DEADLINE_MARKER_MESSAGE, exception.getMessage());
    }

    // parse("deadline  /by 2019-10-15") (marker present but nothing but whitespace
    // before it) should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_deadlineBlankDescriptionBeforeMarker_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class,
                () -> CommandParser.parse("deadline  /by 2019-10-15"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // parse("deadline return book /by not-a-date") should throw AlzaraException
    // with message AlzaraException.INVALID_DEADLINE_DATE_MESSAGE.
    @Test
    void parse_deadlineInvalidDate_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class,
                () -> CommandParser.parse("deadline return book /by not-a-date"));
        assertEquals(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE, exception.getMessage());
    }

    // --- event ---

    // parse("event project meeting /from 2019-10-15 /to 2019-10-16") should return
    // an AddCommand, no exception thrown.
    @Test
    void parse_eventWithDescriptionAndDates_returnsAddCommand() {
        Command result = assertDoesNotThrow(
                () -> CommandParser.parse("event project meeting /from 2019-10-15 /to 2019-10-16"));
        assertInstanceOf(AddCommand.class, result);
    }

    // parse("event") (bare command) should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_eventMissingDescription_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("event"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // parse("event project meeting") (no " /from "/" /to " markers at all) should
    // throw AlzaraException with message AlzaraException.MISSING_EVENT_MARKER_MESSAGE.
    @Test
    void parse_eventMissingFromToMarkers_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class,
                () -> CommandParser.parse("event project meeting"));
        assertEquals(AlzaraException.MISSING_EVENT_MARKER_MESSAGE, exception.getMessage());
    }

    // parse("event project meeting /to 2019-10-16 /from 2019-10-15") (both markers
    // present, but /to appears before /from) should throw AlzaraException with
    // message AlzaraException.MISSING_EVENT_MARKER_MESSAGE.
    @Test
    void parse_eventToBeforeFrom_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class,
                () -> CommandParser.parse("event project meeting /to 2019-10-16 /from 2019-10-15"));
        assertEquals(AlzaraException.MISSING_EVENT_MARKER_MESSAGE, exception.getMessage());
    }

    // parse("event  /from 2019-10-15 /to 2019-10-16") (markers present but nothing
    // but whitespace before them) should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_eventBlankDescriptionBeforeMarker_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class,
                () -> CommandParser.parse("event  /from 2019-10-15 /to 2019-10-16"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // parse("event project meeting /from not-a-date /to 2019-10-16") should throw
    // AlzaraException with message AlzaraException.INVALID_DEADLINE_DATE_MESSAGE.
    @Test
    void parse_eventInvalidStartDate_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class,
                () -> CommandParser.parse("event project meeting /from not-a-date /to 2019-10-16"));
        assertEquals(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE, exception.getMessage());
    }

    // parse("event project meeting /from 2019-10-15 /to not-a-date") should throw
    // AlzaraException with message AlzaraException.INVALID_DEADLINE_DATE_MESSAGE.
    @Test
    void parse_eventInvalidEndDate_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class,
                () -> CommandParser.parse("event project meeting /from 2019-10-15 /to not-a-date"));
        assertEquals(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE, exception.getMessage());
    }

    // --- find ---

    // parse("find book") should return a FindCommand, no exception thrown.
    @Test
    void parse_findWithKeyword_returnsFindCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("find book"));
        assertInstanceOf(FindCommand.class, result);
    }

    // parse("find") (no keyword at all) should throw AlzaraException with
    // message AlzaraException.MISSING_KEYWORD_MESSAGE.
    @Test
    void parse_findMissingKeyword_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("find"));
        assertEquals(AlzaraException.MISSING_KEYWORD_MESSAGE, exception.getMessage());
    }

    // --- unrecognised input ---

    // parse("/list") (matches no known command) should throw AlzaraException with
    // message AlzaraException.UNRECOGNISED_COMMAND_MESSAGE.
    @Test
    void parse_unrecognisedCommand_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("/list"));
        assertEquals(AlzaraException.UNRECOGNISED_COMMAND_MESSAGE, exception.getMessage());
    }
}

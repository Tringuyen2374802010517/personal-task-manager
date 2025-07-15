package PersonalTaskManagerViolations;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class codeDaRefactor {
	private static final String DB_FILE_PATH = "tasks_database.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Load tasks
    private static JSONArray loadTasks() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(DB_FILE_PATH)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) return (JSONArray) obj;
        } catch (IOException | ParseException e) {
            System.err.println("Error reading database: " + e.getMessage());
        }
        return new JSONArray();
    }

    // Save tasks
    private static void saveTasks(JSONArray tasksData) {
        try (FileWriter file = new FileWriter(DB_FILE_PATH)) {
            file.write(tasksData.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.err.println("Error writing database: " + e.getMessage());
        }
    }

    // Validate priority
    private static boolean isValidPriority(String priorityLevel) {
        return priorityLevel.equals("Thấp") || priorityLevel.equals("Trung bình") || priorityLevel.equals("Cao");
    }

    // Validate inputs
    private static LocalDate validateTaskInputs(String title, String dueDateStr, String priorityLevel) {
        if (title == null || title.trim().isEmpty()) {
            System.out.println("Error: Title cannot be empty.");
            return null;
        }
        if (dueDateStr == null || dueDateStr.trim().isEmpty()) {
            System.out.println("Error: Due date cannot be empty.");
            return null;
        }
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Use YYYY-MM-DD.");
            return null;
        }
        if (!isValidPriority(priorityLevel)) {
            System.out.println("Error: Invalid priority. Choose Thấp, Trung bình, or Cao.");
            return null;
        }
        return dueDate;
    }

    // Add new task (refactored)
    public JSONObject addNewTask(String title, String description, String dueDateStr, String priorityLevel) {
        LocalDate dueDate = validateTaskInputs(title, dueDateStr, priorityLevel);
        if (dueDate == null) return null;

        JSONArray tasks = loadTasks();

        // Check duplicate
        for (Object obj : tasks) {
            JSONObject existingTask = (JSONObject) obj;
            if (existingTask.get("title").toString().equalsIgnoreCase(title) &&
                existingTask.get("due_date").toString().equals(dueDate.format(DATE_FORMATTER))) {
                System.out.printf("Error: Task '%s' already exists with same due date.\n", title);
                return null;
            }
        }

        // Create new task
        String taskId = UUID.randomUUID().toString();
        JSONObject newTask = new JSONObject();
        newTask.put("id", taskId);
        newTask.put("title", title);
        newTask.put("description", description);
        newTask.put("due_date", dueDate.format(DATE_FORMATTER));
        newTask.put("priority", priorityLevel);
        newTask.put("status", "Chưa hoàn thành");

        tasks.add(newTask);
        saveTasks(tasks);

        System.out.printf("Task added successfully with ID: %s\n", taskId);
        return newTask;
    }

    public static void main(String[] args) {
        PersonalTaskManager manager = new PersonalTaskManager();
        manager.addNewTask("Học Java", "Ôn SOLID, KISS, DRY", "2025-07-16", "Cao");
    }
}
	

}

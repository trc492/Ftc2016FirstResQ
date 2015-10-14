package trclib;

import java.util.ArrayList;
import java.util.HashSet;

public class TrcTaskMgr
{
    private static final String moduleName = "TrcTaskMgr";
    private static final boolean debugEnabled = false;
    private static TrcDbgTrace dbgTrace =
            debugEnabled? new TrcDbgTrace(
                    moduleName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO):
                    null;

    public enum TaskType
    {
        START_TASK,
        STOP_TASK,
        PREPERIODIC_TASK,
        POSTPERIODIC_TASK,
        PRECONTINUOUS_TASK,
        POSTCONTINUOUS_TASK
    }   //enum TaskType

    public interface Task
    {
        public void startTask(TrcRobot.RunMode runMode);
        public void stopTask(TrcRobot.RunMode runMode);
        public void prePeriodicTask(TrcRobot.RunMode runMode);
        public void postPeriodicTask(TrcRobot.RunMode runMode);
        public void preContinuousTask(TrcRobot.RunMode runMode);
        public void postContinuousTask(TrcRobot.RunMode runMode);
    }   //interface Task

    private static class SubsystemTask
    {
        private HashSet<TaskType> taskTypes;
        private String taskName;
        private Task task;

        public SubsystemTask(String taskName, Task task)
        {
            taskTypes = new HashSet<TaskType>();
            this.taskName = taskName;
            this.task = task;
        }   //SubsystemTask

        public boolean addTaskType(TaskType type)
        {
            return taskTypes.add(type);
        }   //addTaskType

        public boolean removeTaskType(TaskType type)
        {
            return taskTypes.remove(type);
        }   //removeTaskType

        public boolean isSame(Task task)
        {
            return task == this.task;
        }   //isSame

        public boolean hasType(TaskType type)
        {
            return taskTypes.contains(type);
        }   //hasType

        public boolean hasNoType()
        {
            return taskTypes.isEmpty();
        }   //hasNoType

        public String getName()
        {
            return taskName;
        }   //getName

        public Task getTask()
        {
            return task;
        }   //getTask

    }   //class SubsystemTask

    private static ArrayList<SubsystemTask> taskList =
            new ArrayList<SubsystemTask>();

    public static boolean registerTask(
            String taskName,
            Task task,
            TaskType type)
    {
        final String funcName = "registerTask";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "task=%s,type=%s", taskName, type.toString());
        }

        SubsystemTask subsystemTask = findTask(task);

        if (subsystemTask == null)
        {
            subsystemTask = new SubsystemTask(taskName, task);
            taskList.add(subsystemTask);
        }

        if (subsystemTask != null)
        {
            subsystemTask.addTaskType(type);
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%s", Boolean.toString(subsystemTask != null));
        }

        return subsystemTask != null;
    }   //registerTask

    public static void unregisterTask(Task task, TaskType type)
    {
        final String funcName = "unregisterTask";
        SubsystemTask subsystemTask = findTask(task);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "task=%s,type=%s",
                    subsystemTask != null? subsystemTask.getName(): "unknown",
                    type.toString());
        }

        if (subsystemTask != null)
        {
            subsystemTask.removeTaskType(type);
            if (subsystemTask.hasNoType())
            {
                taskList.remove(subsystemTask);
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //unregisterTask

    public static void executeTaskType(TaskType type, TrcRobot.RunMode mode)
    {
        final String funcName = "executeTaskType";

        for (int i = 0; i < taskList.size(); i++)
        {
            SubsystemTask subsystemTask = taskList.get(i);
            if (subsystemTask.hasType(type))
            {
                Task task = subsystemTask.getTask();
                switch (type)
                {
                    case START_TASK:
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(
                                    funcName,
                                    "Executing StartTask %s",
                                    subsystemTask.getName());
                        }
                        task.startTask(mode);
                        break;

                    case STOP_TASK:
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(
                                    funcName,
                                    "Executing StopTask %s",
                                    subsystemTask.getName());
                        }
                        task.stopTask(mode);
                        break;

                    case PREPERIODIC_TASK:
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(
                                    funcName,
                                    "Executing PrePeriodicTask %s",
                                    subsystemTask.getName());
                        }
                        task.prePeriodicTask(mode);
                        break;

                    case POSTPERIODIC_TASK:
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(
                                    funcName,
                                    "Executing PostPeriodicTask %s",
                                    subsystemTask.getName());
                        }
                        task.postPeriodicTask(mode);
                        break;

                    case PRECONTINUOUS_TASK:
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(
                                    funcName,
                                    "Executing PreContinuousTask %s",
                                    subsystemTask.getName());
                        }
                        task.preContinuousTask(mode);
                        break;

                    case POSTCONTINUOUS_TASK:
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(
                                    funcName,
                                    "Executing PostContinuousTask %s",
                                    subsystemTask.getName());
                        }
                        task.postContinuousTask(mode);
                        break;
                }
            }
        }
    }   //executeTaskType

    private static SubsystemTask findTask(Task task)
    {
        for (int i = 0; i < taskList.size(); i++)
        {
            SubsystemTask subsystemTask = taskList.get(i);
            if (subsystemTask.isSame(task))
            {
                return subsystemTask;
            }
        }
        return null;
    }   //findTask

}   //class TaskMgr

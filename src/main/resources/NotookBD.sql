
USE master;
GO

IF DB_ID('notook_db') IS NULL
    CREATE DATABASE notook_db;
GO

USE notook_db;
GO


-- USER_ACCOUNT
CREATE TABLE user_account (
    id_user BIGINT IDENTITY(1,1) NOT NULL,
    user_name NVARCHAR(80) NOT NULL,
    email NVARCHAR(100) NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_user_account_created_at DEFAULT (SYSDATETIME()),
    is_active BIT NOT NULL CONSTRAINT DF_user_account_is_active DEFAULT (1),

    -- Clave primaria
    CONSTRAINT PK_user_account PRIMARY KEY (id_user),

    -- Restricciones de unicidad
    CONSTRAINT UQ_user_account_user_name UNIQUE (user_name),
    CONSTRAINT UQ_user_account_email UNIQUE (email),

    -- Reglas de validación
    CONSTRAINT CHK_user_account_user_name CHECK (
        LEN(user_name) >= 5
        AND LEFT(user_name, 1) LIKE N'[A-Za-z0-9]'
    )
);
GO


-- COURSE
CREATE TABLE course (
    id_course BIGINT IDENTITY(1,1) NOT NULL,
    id_user BIGINT NOT NULL,
    course_name NVARCHAR(80) NOT NULL,
    description NVARCHAR(255) NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_course_created_at DEFAULT (SYSDATETIME()),
    start_date DATE NULL,
    end_date DATE NULL,
    is_archived BIT NOT NULL CONSTRAINT DF_course_is_archived DEFAULT (0),

    -- Clave primaria
    CONSTRAINT PK_course PRIMARY KEY (id_course),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_course_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),

    -- Reglas de validación
    CONSTRAINT CHK_course_dates CHECK (
        end_date IS NULL
        OR start_date IS NULL
        OR end_date >= start_date
    )
);
GO


-- PROJECT
CREATE TABLE project (
    id_project BIGINT IDENTITY(1,1) NOT NULL,
    id_created_by BIGINT NOT NULL,
    id_course BIGINT NULL,
    project_name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500) NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_project_created_at DEFAULT (SYSDATETIME()),
    start_date DATE NULL,
    due_date DATE NULL,
    state NVARCHAR(20) NOT NULL CONSTRAINT DF_project_state DEFAULT (N'ACTIVO'),
    is_archived BIT NOT NULL CONSTRAINT DF_project_is_archived DEFAULT (0),

    -- Clave primaria
    CONSTRAINT PK_project PRIMARY KEY (id_project),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_project_created_by FOREIGN KEY (id_created_by) REFERENCES user_account(id_user),
    CONSTRAINT FK_project_course FOREIGN KEY (id_course) REFERENCES course(id_course),

    -- Reglas de validación
    CONSTRAINT CHK_project_dates CHECK (
        due_date IS NULL
        OR start_date IS NULL
        OR due_date >= start_date
    ),
    CONSTRAINT CHK_project_state CHECK (
        state IN (
            N'PLANEADO',
            N'ACTIVO',
            N'FINALIZADO',
            N'CANCELADO'
        )
    )
);
GO


CREATE TABLE role (
	id_role TINYINT NOT NULL,
	role_name VARCHAR(13) NOT NULL,
	CONSTRAINT PK_role PRIMARY KEY (id_role),
	CONSTRAINT CHK_role CHECK (
		role_name IN (
			N'ADMINISTRADOR',
			N'MODERADOR',
			N'MIEMBRO'
		)
	)
)


-- PROJECT_MEMBER
CREATE TABLE project_member (
    id_project BIGINT NOT NULL,
    id_user BIGINT NOT NULL,
    id_role TINYINT NOT NULL,
    joined_at DATETIME2(0) NOT NULL CONSTRAINT DF_project_member_joined_at DEFAULT (SYSDATETIME()),
    is_active BIT NOT NULL CONSTRAINT DF_project_member_is_active DEFAULT (1),

    -- Clave primaria
    CONSTRAINT PK_project_member PRIMARY KEY (id_project, id_user),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_project_member_project FOREIGN KEY (id_project) REFERENCES project(id_project),
    CONSTRAINT FK_project_member_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),
	CONSTRAINT FK_proyect_member_role FOREIGN KEY (id_role) REFERENCES role(id_role)
);
GO


-- DELIVERABLE
CREATE TABLE deliverable (
    id_deliverable BIGINT IDENTITY(1,1) NOT NULL,
    id_project BIGINT NOT NULL,
    title NVARCHAR(100) NOT NULL,
    description NVARCHAR(500) NULL,
    due_at DATETIME2(0) NULL,
    state NVARCHAR(20) NOT NULL CONSTRAINT DF_deliverable_state DEFAULT (N'PENDIENTE'),
    position SMALLINT NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_deliverable_created_at DEFAULT (SYSDATETIME()),

    -- Clave primaria
    CONSTRAINT PK_deliverable PRIMARY KEY (id_deliverable),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_deliverable_project FOREIGN KEY (id_project) REFERENCES project(id_project),

    -- Reglas de validación
    CONSTRAINT CHK_deliverable_state CHECK (
        state IN (
            N'PENDIENTE',
            N'EN_PROGRESO',
            N'FINALIZADO'
        )
    ),
    CONSTRAINT CHK_deliverable_position CHECK (
        position > 0
    )
);
GO


-- TASK
CREATE TABLE task (
    id_task BIGINT IDENTITY(1,1) NOT NULL,
    id_user BIGINT NOT NULL,
    id_course BIGINT NULL,
    id_project BIGINT NULL,
    id_deliverable BIGINT NULL,
    id_assigned_user BIGINT NULL,
    task_name NVARCHAR(80) NOT NULL,
    description NVARCHAR(255) NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_task_created_at DEFAULT (SYSDATETIME()),
    due_at DATETIME2(0) NULL,
    started_at DATETIME2(0) NULL,
    completed_at DATETIME2(0) NULL,
    priority VARCHAR(5) NOT NULL,
    state NVARCHAR(20) NOT NULL CONSTRAINT DF_task_state DEFAULT (N'NO_INICIADO'),
    is_archived BIT NOT NULL CONSTRAINT DF_task_is_archived DEFAULT (0),
	is_begun BIT NOT NULL CONSTRAINT DF_task_is_begun DEFAULT (0),
    -- Clave primaria
    CONSTRAINT PK_task PRIMARY KEY (id_task),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_task_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),
    CONSTRAINT FK_task_course FOREIGN KEY (id_course) REFERENCES course(id_course),
    CONSTRAINT FK_task_project FOREIGN KEY (id_project) REFERENCES project(id_project),
    CONSTRAINT FK_task_deliverable FOREIGN KEY (id_deliverable) REFERENCES deliverable(id_deliverable),
    CONSTRAINT FK_task_assigned_user FOREIGN KEY (id_assigned_user) REFERENCES user_account(id_user),

    -- Reglas de validación
    CONSTRAINT CHK_task_priority CHECK (
        priority IN (
			N'NINGUNO',
			N'BAJA',
			N'MEDIA',
			N'ALTA',
		)
    ),
    CONSTRAINT CHK_task_state CHECK (
        state IN (
            N'NO_INICIADO',
            N'EN_PROGRESO',
            N'EN_PAUSA',
            N'BLOQUEADA',
            N'FINALIZADA'
        )
    ),
    CONSTRAINT CHK_task_dates CHECK (
        completed_at IS NULL
        OR started_at IS NULL
        OR completed_at >= started_at
    ),
    CONSTRAINT CHK_task_deliverable_context CHECK (
        id_deliverable IS NULL
        OR id_project IS NOT NULL
    )
);
GO


-- TASK_DEPENDENCY
CREATE TABLE task_dependency (
    id_task BIGINT NOT NULL,
    id_depends_on_task BIGINT NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_task_dependency_created_at DEFAULT (SYSDATETIME()),

    -- Clave primaria
    CONSTRAINT PK_task_dependency PRIMARY KEY (id_task, id_depends_on_task),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_task_dependency_task FOREIGN KEY (id_task) REFERENCES task(id_task),
    CONSTRAINT FK_task_dependency_depends_on FOREIGN KEY (id_depends_on_task) REFERENCES task(id_task),

    -- Reglas de validación
    CONSTRAINT CHK_task_dependency_different_task CHECK (
        id_task <> id_depends_on_task
    )
);
GO


-- KANBAN_BOARD
CREATE TABLE kanban_board (
    id_kanban_board BIGINT IDENTITY(1,1) NOT NULL,
    id_user BIGINT NOT NULL,
    id_project BIGINT NULL,
    title NVARCHAR(80) NOT NULL CONSTRAINT DF_kanban_board_title DEFAULT (N'Tablero'),
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_kanban_board_created_at DEFAULT (SYSDATETIME()),
    saved_at DATETIME2(0) NULL,
    state NVARCHAR(15) NOT NULL CONSTRAINT DF_kanban_board_state DEFAULT (N'ACTIVO'),

    -- Clave primaria
    CONSTRAINT PK_kanban_board PRIMARY KEY (id_kanban_board),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_kanban_board_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),
    CONSTRAINT FK_kanban_board_project FOREIGN KEY (id_project) REFERENCES project(id_project),

    -- Reglas de validación
    CONSTRAINT CHK_kanban_board_state CHECK (
        state IN (
            N'ACTIVO',
            N'GUARDADO'
        )
    )
);
GO


-- KANBAN_COLUMN
CREATE TABLE kanban_column (
    id_kanban_column BIGINT IDENTITY(1,1) NOT NULL,
    id_kanban_board BIGINT NOT NULL,
    name NVARCHAR(40) NOT NULL,
    position SMALLINT NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_kanban_column_created_at DEFAULT (SYSDATETIME()),

    -- Clave primaria
    CONSTRAINT PK_kanban_column PRIMARY KEY (id_kanban_column),

    -- Restricciones de unicidad
    CONSTRAINT UQ_kanban_column_board_name UNIQUE (id_kanban_board, name),
    CONSTRAINT UQ_kanban_column_board_position UNIQUE (id_kanban_board, position),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_kanban_column_board FOREIGN KEY (id_kanban_board) REFERENCES kanban_board(id_kanban_board),

    -- Reglas de validación
    CONSTRAINT CHK_kanban_column_position CHECK (
        position > 0
    )
);
GO


-- KANBAN_BOARD_TASK
CREATE TABLE kanban_board_task (
    id_kanban_board BIGINT NOT NULL,
    id_task BIGINT NOT NULL,
    id_kanban_column BIGINT NOT NULL,
    position SMALLINT NOT NULL CONSTRAINT DF_kanban_board_task_position DEFAULT (1),

    -- Clave primaria
    CONSTRAINT PK_kanban_board_task PRIMARY KEY (id_kanban_board, id_task),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_kanban_board_task_board FOREIGN KEY (id_kanban_board) REFERENCES kanban_board(id_kanban_board),
    CONSTRAINT FK_kanban_board_task_task FOREIGN KEY (id_task) REFERENCES task(id_task),
    CONSTRAINT FK_kanban_board_task_column FOREIGN KEY (id_kanban_column) REFERENCES kanban_column(id_kanban_column),

    -- Reglas de validación
    CONSTRAINT CHK_kanban_board_task_position CHECK (
        position > 0
    )
);
GO


-- RECURRING_ACTIVITY
CREATE TABLE recurring_activity (
    id_activity BIGINT IDENTITY(1,1) NOT NULL,
    id_user BIGINT NOT NULL,
    id_course BIGINT NULL,
    title NVARCHAR(80) NOT NULL,
    start_time TIME(0) NOT NULL,
    end_time TIME(0) NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_recurring_activity_created_at DEFAULT (SYSDATETIME()),

    -- Clave primaria
    CONSTRAINT PK_recurring_activity PRIMARY KEY (id_activity),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_recurring_activity_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),
    CONSTRAINT FK_recurring_activity_course FOREIGN KEY (id_course) REFERENCES course(id_course),

    -- Reglas de validación
    CONSTRAINT CHK_recurring_activity_time CHECK (
        end_time > start_time
    )
);
GO


-- RECURRING_ACTIVITY_DAY
CREATE TABLE recurring_activity_day (
    id_activity BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL,

    -- Clave primaria
    CONSTRAINT PK_recurring_activity_day PRIMARY KEY (id_activity, day_of_week),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_recurring_activity_day_activity FOREIGN KEY (id_activity) REFERENCES recurring_activity(id_activity),

    -- Reglas de validación
    CONSTRAINT CHK_recurring_activity_day CHECK (
        day_of_week BETWEEN 1 AND 7
    )
);
GO


-- CALENDAR_EVENT
CREATE TABLE calendar_event (
    id_event BIGINT IDENTITY(1,1) NOT NULL,
    id_user BIGINT NOT NULL,
    id_course BIGINT NULL,
    title NVARCHAR(80) NOT NULL,
    description NVARCHAR(255) NULL,
    start_at DATETIME2(0) NOT NULL,
    end_at DATETIME2(0) NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_calendar_event_created_at DEFAULT (SYSDATETIME()),

    -- Clave primaria
    CONSTRAINT PK_calendar_event PRIMARY KEY (id_event),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_calendar_event_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),
    CONSTRAINT FK_calendar_event_course FOREIGN KEY (id_course) REFERENCES course(id_course),

    -- Reglas de validación
    CONSTRAINT CHK_calendar_event_dates CHECK (
        end_at IS NULL
        OR end_at > start_at
    )
);
GO


-- NOTE
CREATE TABLE note (
    id_note BIGINT IDENTITY(1,1) NOT NULL,
    id_user BIGINT NOT NULL,
    id_course BIGINT NULL,
    title NVARCHAR(80) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_note_created_at DEFAULT (SYSDATETIME()),
    updated_at DATETIME2(0) NOT NULL CONSTRAINT DF_note_updated_at DEFAULT (SYSDATETIME()),
    is_archived BIT NOT NULL CONSTRAINT DF_note_is_archived DEFAULT (0),
    is_public BIT NOT NULL CONSTRAINT DF_note_is_public DEFAULT (0),

    -- Clave primaria
    CONSTRAINT PK_note PRIMARY KEY (id_note),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_note_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),
    CONSTRAINT FK_note_course FOREIGN KEY (id_course) REFERENCES course(id_course)
);
GO


-- FAVORITE_NOTE
CREATE TABLE favorite_note (
    id_user BIGINT NOT NULL,
    id_note BIGINT NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_favorite_note_created_at DEFAULT (SYSDATETIME()),

    -- Clave primaria
    CONSTRAINT PK_favorite_note PRIMARY KEY (id_user, id_note),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_favorite_note_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),
    CONSTRAINT FK_favorite_note_note FOREIGN KEY (id_note) REFERENCES note(id_note)
);
GO


-- STUDY_SESSION
CREATE TABLE study_session (
    id_session BIGINT IDENTITY(1,1) NOT NULL,
    id_user BIGINT NOT NULL,
    id_course BIGINT NULL,
    id_task BIGINT NULL,
    mode NVARCHAR(15) NOT NULL,
    focus_duration_minutes SMALLINT NOT NULL,
    break_duration_minutes SMALLINT NOT NULL,
    planned_cycles TINYINT NOT NULL,
    completed_cycles TINYINT NOT NULL CONSTRAINT DF_study_session_completed_cycles DEFAULT (0),
    effective_study_seconds INT NOT NULL CONSTRAINT DF_study_session_effective_seconds DEFAULT (0),
    started_at DATETIME2(0) NOT NULL CONSTRAINT DF_study_session_started_at DEFAULT (SYSDATETIME()),
    finished_at DATETIME2(0) NULL,
    state NVARCHAR(15) NOT NULL CONSTRAINT DF_study_session_state DEFAULT (N'ACTIVA'),

    -- Clave primaria
    CONSTRAINT PK_study_session PRIMARY KEY (id_session),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_study_session_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),
    CONSTRAINT FK_study_session_course FOREIGN KEY (id_course) REFERENCES course(id_course),
    CONSTRAINT FK_study_session_task FOREIGN KEY (id_task) REFERENCES task(id_task),

    -- Reglas de validación
    CONSTRAINT CHK_study_session_mode CHECK (
        mode IN (
            N'POMODORO',
            N'PERSONALIZADO'
        )
    ),
    CONSTRAINT CHK_study_session_state CHECK (
        state IN (
            N'ACTIVA',
            N'PAUSADA',
            N'FINALIZADA',
            N'CANCELADA'
        )
    ),
    CONSTRAINT CHK_study_session_focus_duration CHECK (
        focus_duration_minutes > 0
    ),
    CONSTRAINT CHK_study_session_break_duration CHECK (
        break_duration_minutes > 0
    ),
    CONSTRAINT CHK_study_session_cycles CHECK (
        planned_cycles > 0
        AND completed_cycles >= 0
        AND completed_cycles <= planned_cycles
    ),
    CONSTRAINT CHK_study_session_effective_seconds CHECK (
        effective_study_seconds >= 0
    ),
    CONSTRAINT CHK_study_session_dates CHECK (
        finished_at IS NULL
        OR finished_at >= started_at
    )
);
GO


-- ACADEMIC_GOAL
CREATE TABLE academic_goal (
    id_goal BIGINT IDENTITY(1,1) NOT NULL,
    id_user BIGINT NOT NULL,
    id_course BIGINT NULL,
    title NVARCHAR(100) NOT NULL,
    description NVARCHAR(500) NOT NULL,
    measurement_type NVARCHAR(20) NOT NULL,
    target_value DECIMAL(12,2) NOT NULL,
    current_value DECIMAL(12,2) NOT NULL CONSTRAINT DF_academic_goal_current_value DEFAULT (0),
    start_date DATE NOT NULL,
    deadline DATE NOT NULL,
    state NVARCHAR(15) NOT NULL CONSTRAINT DF_academic_goal_state DEFAULT (N'ACTIVO'),
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_academic_goal_created_at DEFAULT (SYSDATETIME()),
    last_progress_at DATETIME2(0) NULL,
    achieved_at DATETIME2(0) NULL,

    -- Clave primaria
    CONSTRAINT PK_academic_goal PRIMARY KEY (id_goal),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_academic_goal_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),
    CONSTRAINT FK_academic_goal_course FOREIGN KEY (id_course) REFERENCES course(id_course),

    -- Reglas de validación
    CONSTRAINT CHK_academic_goal_measurement_type CHECK (
        measurement_type IN (
            N'TIEMPO',
            N'ACTIVIDADES',
            N'TAREAS',
            N'EVENTOS',
            N'SI_NO'
        )
    ),
    CONSTRAINT CHK_academic_goal_state CHECK (
        state IN (
            N'ACTIVO',
            N'ALCANZADO',
            N'CANCELADO'
        )
    ),
    CONSTRAINT CHK_academic_goal_target CHECK (
        target_value > 0
    ),
    CONSTRAINT CHK_academic_goal_current_value CHECK (
        current_value >= 0
    ),
    CONSTRAINT CHK_academic_goal_dates CHECK (
        deadline >= start_date
    )
);
GO


-- GOAL_REMINDER
CREATE TABLE goal_reminder (
    id_reminder BIGINT IDENTITY(1,1) NOT NULL,
    id_goal BIGINT NOT NULL,
    is_enabled BIT NOT NULL CONSTRAINT DF_goal_reminder_is_enabled DEFAULT (1),
    frequency_type NVARCHAR(20) NOT NULL,
    interval_days SMALLINT NULL,
    next_reminder_at DATETIME2(0) NULL,

    -- Clave primaria
    CONSTRAINT PK_goal_reminder PRIMARY KEY (id_reminder),

    -- Restricciones de unicidad
    CONSTRAINT UQ_goal_reminder_goal UNIQUE (id_goal),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_goal_reminder_academic_goal FOREIGN KEY (id_goal) REFERENCES academic_goal(id_goal),

    -- Reglas de validación
    CONSTRAINT CHK_goal_reminder_frequency CHECK (
        frequency_type IN (
            N'DIARIA',
            N'SEMANAL',
            N'CADA_N_DIAS',
            N'PERSONALIZADA'
        )
    ),
    CONSTRAINT CHK_goal_reminder_interval CHECK (
        interval_days IS NULL
        OR interval_days > 0
    )
);
GO


-- NOTIFICATION
CREATE TABLE notification (
    id_notification BIGINT IDENTITY(1,1) NOT NULL,
    id_user BIGINT NOT NULL,
    notification_type NVARCHAR(30) NOT NULL,
    title NVARCHAR(100) NOT NULL,
    message NVARCHAR(500) NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_notification_created_at DEFAULT (SYSDATETIME()),
    is_read BIT NOT NULL CONSTRAINT DF_notification_is_read DEFAULT (0),
    read_at DATETIME2(0) NULL,

    -- Clave primaria
    CONSTRAINT PK_notification PRIMARY KEY (id_notification),

    -- Relaciones y claves foráneas
    CONSTRAINT FK_notification_user_account FOREIGN KEY (id_user) REFERENCES user_account(id_user),

    -- Reglas de validación
    CONSTRAINT CHK_notification_type CHECK (
        notification_type IN (
            N'TAREA_PROXIMA',
            N'TAREA_VENCIDA',
            N'EVENTO_PROXIMO',
            N'FIN_CONCENTRACION',
            N'FIN_DESCANSO',
            N'RECORDATORIO_OBJETIVO'
        )
    ),
    CONSTRAINT CHK_notification_read CHECK (
        (is_read = 0 AND read_at IS NULL)
        OR
        (is_read = 1 AND read_at IS NOT NULL)
    )
);
GO

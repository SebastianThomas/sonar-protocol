CREATE TABLE t_ships
(
    pk_ship_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    health     INTEGER NOT NULL
);

CREATE TABLE t_switch
(
    fk_ship_id INTEGER REFERENCES t_ships (pk_ship_id) NOT NULL,
    action     TEXT                                    NOT NULL,
    position   INTEGER                                 NOT NULL DEFAULT 0,
    PRIMARY KEY (fk_ship_id, action)
);

CREATE TABLE t_teams
(
    pk_team_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_ship_id INTEGER REFERENCES t_ships (pk_ship_id) NOT NULL
);

CREATE TABLE t_games
(
    pk_game_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_team_a  INTEGER REFERENCES t_teams (pk_team_id) NOT NULL,
    fk_team_b  INTEGER REFERENCES t_teams (pk_team_id) NOT NULL,
    state      TEXT                                    NOT NULL
);

CREATE TABLE t_players
(
    pk_player_id  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_team_id    INTEGER REFERENCES t_teams (pk_team_id) NOT NULL,
    name          TEXT                                    NOT NULL,
    role          TEXT                                    NOT NULL,
    ws_session_id TEXT                                    NOT NULL
);

CREATE TABLE t_paths
(
    pk_path_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_ship_id INTEGER REFERENCES t_ships (pk_ship_id) NOT NULL,
    surfaced   BOOLEAN DEFAULT FALSE                   NOT NULL
);

CREATE TABLE t_path_nodes
(
    pk_path_node_id  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_path_id       INTEGER REFERENCES t_paths (pk_path_id) NOT NULL,
    x                INTEGER                                 NOT NULL,
    y                INTEGER                                 NOT NULL,
    time             TIMESTAMPTZ                             NOT NULL,
    switch_activated BOOLEAN                                 NOT NULL DEFAULT FALSE
);

CREATE TABLE t_ships
(
    pk_ship_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

CREATE TABLE t_teams
(
    pk_team_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_ship_id INTEGER REFERENCES t_ships (pk_ship_id)
);

CREATE TABLE t_games
(
    pk_game_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_team_a  INTEGER REFERENCES t_teams (pk_team_id),
    fk_team_b  INTEGER REFERENCES t_teams (pk_team_id),
    state      TEXT NOT NULL
);

CREATE TABLE t_players
(
    pk_player_id  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_team_id    INTEGER REFERENCES t_teams (pk_team_id),
    name          TEXT NOT NULL,
    role          TEXT NOT NULL,
    ws_session_id TEXT NOT NULL
);

CREATE TABLE t_paths
(
    pk_path_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_ship_id INTEGER REFERENCES t_ships (pk_ship_id),
    surfaced   BOOLEAN DEFAULT FALSE
);

CREATE TABLE t_path_nodes
(
    pk_path_node_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fk_path_id      INTEGER REFERENCES t_paths (pk_path_id),
    x               INTEGER     NOT NULL,
    y               INTEGER     NOT NULL,
    time            TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS scenarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hub_id VARCHAR, -- Хаб, к которому применим сценарий
    name VARCHAR, -- Уникальное название сценария в рамках хаба
    UNIQUE(hub_id, name)
);

CREATE TABLE IF NOT EXISTS sensors (
    id VARCHAR PRIMARY KEY,
    hub_id VARCHAR -- Хаб, которому принадлежит датчик
);

CREATE TABLE IF NOT EXISTS conditions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR, -- Тип условия, например: движение, освещённость, переключатель, температура, CO2 или влажность
    operation VARCHAR, -- Операторы, разрешённые для построения условий, такие как равно, больше или меньше
    value INTEGER -- Значение для сравнения в условии
);

CREATE TABLE IF NOT EXISTS actions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR, -- Действия, выполняемые устройством в рамках сценария (активировать, деактивировать, инвертировать или установить значение)
    value INTEGER -- Значение, которое устанавливается при выполнении действия (если применимо)
);

CREATE TABLE IF NOT EXISTS scenario_conditions (
    scenario_id BIGINT REFERENCES scenarios(id),
    sensor_id VARCHAR REFERENCES sensors(id),
    condition_id BIGINT REFERENCES conditions(id),
    PRIMARY KEY (scenario_id, sensor_id, condition_id)
);

CREATE TABLE IF NOT EXISTS scenario_actions (
    scenario_id BIGINT REFERENCES scenarios(id),
    sensor_id VARCHAR REFERENCES sensors(id),
    action_id BIGINT REFERENCES actions(id),
    PRIMARY KEY (scenario_id, sensor_id, action_id)
);

-- Функция для проверки того, что связанный сценарий и датчик принадлежат одному хабу
CREATE OR REPLACE FUNCTION check_hub_id()
RETURNS TRIGGER AS
'
BEGIN
    IF (SELECT hub_id FROM scenarios WHERE id = NEW.scenario_id) != (SELECT hub_id FROM sensors WHERE id = NEW.sensor_id) THEN
        RAISE EXCEPTION ''Hub IDs do not match for scenario_id % and sensor_id %'', NEW.scenario_id, NEW.sensor_id;
    END IF;
    RETURN NEW;
END;
'
LANGUAGE plpgsql;

-- Триггер для проверки, что "условие" связывает правильный сценарий и датчик
CREATE OR REPLACE TRIGGER tr_bi_scenario_conditions_hub_id_check
BEFORE INSERT ON scenario_conditions
FOR EACH ROW
EXECUTE FUNCTION check_hub_id();

-- Триггер для проверки, что "действие" связывает правильный сценарий и датчик
CREATE OR REPLACE TRIGGER tr_bi_scenario_actions_hub_id_check
BEFORE INSERT ON scenario_actions
FOR EACH ROW
EXECUTE FUNCTION check_hub_id();


CREATE TABLE `channel` (
  `id` bigint NOT NULL,
  `created` varchar(19) NOT NULL,
  `credentials` varchar(1024) DEFAULT NULL,
  `lastUsed` varchar(19) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `config`
--

CREATE TABLE `config` (
  `id` bigint NOT NULL,
  `ckey` varchar(255) NOT NULL,
  `encrypted` bit(1) NOT NULL,
  `value` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Daten für Tabelle `config`
--

INSERT INTO `config` (`id`, `ckey`, `encrypted`, `value`) VALUES
(1, 'image_format', b'0', 'webp'),
(2, 'image_quality', b'0', '0.85');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `data`
--

CREATE TABLE `data` (
  `id` bigint NOT NULL,
  `date` date NOT NULL,
  `failed` int NOT NULL,
  `failingFor` int NOT NULL,
  `firstCheck` varchar(8) NOT NULL,
  `lastCheck` varchar(8) NOT NULL,
  `succeeded` int NOT NULL,
  `monitored_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `monitored`
--

CREATE TABLE `monitored` (
  `id` bigint NOT NULL,
  `alertIfFailingForMin` int NOT NULL,
  `alertSent` bit(1) NOT NULL,
  `checkEveryMin` int NOT NULL,
  `monitor` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `settings` json NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `monitor_error`
--

CREATE TABLE `monitor_error` (
  `id` bigint NOT NULL,
  `dateTime` datetime NOT NULL,
  `message` varchar(255) NOT NULL,
  `monitored_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user`
--

CREATE TABLE `user` (
  `id` bigint NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `channel`
--
ALTER TABLE `channel`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKkg9gk1kf1rcwpt1pnyva9qg3i` (`ckey`);

--
-- Indizes für die Tabelle `data`
--
ALTER TABLE `data`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKpjma8g55k3u7snqxjjxduxjet` (`monitored_id`);

--
-- Indizes für die Tabelle `monitored`
--
ALTER TABLE `monitored`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `monitor_error`
--
ALTER TABLE `monitor_error`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKtebvk19fc97wbb4k6uah3xtrp` (`monitored_id`);

--
-- Indizes für die Tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKsb8bbouer5wak8vyiiy4pf2bx` (`username`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `channel`
--
ALTER TABLE `channel`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `config`
--
ALTER TABLE `config`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT für Tabelle `data`
--
ALTER TABLE `data`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `monitored`
--
ALTER TABLE `monitored`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `monitor_error`
--
ALTER TABLE `monitor_error`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `data`
--
ALTER TABLE `data`
  ADD CONSTRAINT `FKpjma8g55k3u7snqxjjxduxjet` FOREIGN KEY (`monitored_id`) REFERENCES `monitored` (`id`);

--
-- Constraints der Tabelle `monitor_error`
--
ALTER TABLE `monitor_error`
  ADD CONSTRAINT `FKtebvk19fc97wbb4k6uah3xtrp` FOREIGN KEY (`monitored_id`) REFERENCES `monitored` (`id`);
COMMIT;

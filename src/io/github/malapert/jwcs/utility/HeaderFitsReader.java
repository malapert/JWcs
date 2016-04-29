/*
 * Copyright (C) 2016 malapert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.malapert.jwcs.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class HeaderFitsReader {

    private static final String SEPARATOR = "=";

    private final Reader source;

    public HeaderFitsReader(final Reader source) {
        this.source = source;
    }

    public HeaderFitsReader(final String filename) throws IOException {
        Path path = Paths.get(filename);
        Reader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
        this.source = reader;
    }

    public List<List<String>> readKeywords() {
        try (BufferedReader reader = new BufferedReader(source)) {
            return reader.lines()
                    .filter(line -> line.contains("="))
                    .map(line -> Arrays.asList(line.split(SEPARATOR)))
                    .map(line -> {
                        String keyword = line.get(0);
                        String[]valComm = line.get(1).split(" /");
                        String value = valComm[0].replace("'", "");
                        return Arrays.asList(keyword.trim(),value.trim());
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

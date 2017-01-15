/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.modules.junit4.common.internal.impl;

/**
 * This code is copied from http://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
 */
class VersionTokenizer {
    private final String _versionString;
    private final int _length;

    private int _position;
    private int _number;
    private String _suffix;

    public int getNumber() {
        return _number;
    }

    public String getSuffix() {
        return _suffix;
    }

    public VersionTokenizer(String versionString) {
        if (versionString == null)
            throw new IllegalArgumentException("versionString is null");

        _versionString = versionString;
        _length = versionString.length();
    }

    public boolean MoveNext() {
        _number = 0;
        _suffix = "";

        // No more characters
        if (_position >= _length)
            return false;

        while (_position < _length) {
            char c = _versionString.charAt(_position);
            if (c < '0' || c > '9') break;
            _number = _number * 10 + (c - '0');
            _position++;
        }

        int suffixStart = _position;

        while (_position < _length) {
            char c = _versionString.charAt(_position);
            if (c == '.') break;
            _position++;
        }

        _suffix = _versionString.substring(suffixStart, _position);

        if (_position < _length) _position++;

        return true;
    }
}

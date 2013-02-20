/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright (c) 2013 Edugility LLC.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * The original copy of this license is available at
 * http://www.opensource.org/license/mit-license.html.
 */
package com.edugility.splain.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.Reader;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import com.edugility.objexj.Pattern;

import com.edugility.splain.MessageFactory;

public class MessageFactoryReader implements Closeable {

  private static final long serialVersionUID = 1L;

  private static final String LS = System.getProperty("line.separator", "\n");

  private enum State {
    NORMAL,
    BLOCK_COMMENT,
    MATCHERS,
    MESSAGE
  }

  private final LineNumberReader reader;

  public MessageFactoryReader(final Reader reader) {
    super();
    if (reader == null) {
      throw new IllegalArgumentException("reader", new NullPointerException("reader"));
    }
    if (reader instanceof BufferedReader) {
      this.reader = (LineNumberReader)reader;
    } else {
      this.reader = new LineNumberReader(reader);
    }
  }

  public <T> MessageFactory<T> read(final ResourceBundle rb) throws IOException {
    if (rb == null) {
      throw new IllegalArgumentException("rb", new NullPointerException("rb"));
    }
    if (this.reader == null) {
      throw new IllegalStateException("this.reader", new NullPointerException("this.reader"));
    }
    final MessageFactory<T> mf = new MessageFactory<T>(rb);
    State state = State.NORMAL;
    final Set<Pattern<T>> patterns = new LinkedHashSet<Pattern<T>>();
    StringBuilder message = null;
    String line;
    while ((line = this.reader.readLine()) != null) {
      line = line.trim();

      switch (state) {

        // NORMAL
      case NORMAL:
        if (line.isEmpty() || line.startsWith("#")) {
          break;
        } else if (line.startsWith("--")) {
          throw new IllegalStateException("\"--\" is not permitted here at line " + reader.getLineNumber());
        } else {
          state = State.MATCHERS;
          patterns.add(Pattern.<T>compile(line));
          break;
        }
        // end NORMAL


        // MATCHERS
      case MATCHERS:
        if (line.isEmpty()) {
          throw new IllegalStateException("An empty line is not permitted here at line " + reader.getLineNumber());
        } else if (line.startsWith("--")) {
          state = State.MESSAGE;
        } else if (!line.startsWith("#")) {
          patterns.add(Pattern.<T>compile(line));
        }
        break;
        // end MATCHERS


        // MESSAGE
      case MESSAGE:
        if (line.isEmpty()) {
          if (message != null) {
            assert patterns != null;
            assert !patterns.isEmpty();
            mf.addPatterns(message.toString(), patterns);
            patterns.clear();
            message = null;
            state = State.NORMAL;
          }
        } else {
          if (message == null) {
            message = new StringBuilder();
          } else {
            message.append(LS);
          }
          message.append(line);
        }
        break;
        // end MESSAGE


      default:
        throw new IllegalStateException("Unexpected state: " + state);
      }

      if (message != null && !patterns.isEmpty()) {
        mf.addPatterns(message.toString(), patterns);
        patterns.clear();
        message = null;
      }

    }
    return mf;
  }

  @Override
  public void close() throws IOException {
    if (this.reader != null) {
      this.reader.close();
    }
  }

}

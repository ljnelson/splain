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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Serializable; // for javadoc only

import java.sql.SQLException; // for javadoc only

import java.text.ParseException;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.Set;

import com.edugility.objexj.Pattern;

import com.edugility.splain.MessageFactory;
import com.edugility.splain.ResourceBundleKey;

/**
 * Creates {@link MessageFactory} instances from an underlying {@link
 * Reader}.
 *
 * <p>{@link MessageFactoryReader} instances must be {@linkplain
 * #close() closed} after they have been used to {@linkplain #read()
 * produce <code>MessageFactory</code> instances}.</p>
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public class MessageFactoryReader implements Closeable {

  /**
   * This class' version for {@linkplain Serializable serialization
   * purposes}.
   *
   * @see Serializable
   */
  private static final long serialVersionUID = 1L;

  /**
   * This platform's line separator.  This field is never {@code
   * null}.
   */
  private static final String LS = System.getProperty("line.separator", "\n");

  /**
   * A state that the internal parser can be in.
   *
   * @author <a href="http://about.me/lairdnelson"
   * target="_parent">Laird Nelson</a>
   */
  private enum State {

    /**
     * A normal state.  The parser will be expecting comment lines or
     * lines describing patterns.
     */
    NORMAL,

    /**
     * A state in which the parser will be looking for patterns or
     * comments.
     */
    MATCHERS,

    /**
     * A state in which the parser will attempt to read the message
     * key that is affiliated with a set of patterns.
     */
    MESSAGE
  }

  /**
   * The {@link LineNumberReader} that is responsible for reading the
   * text to be parsed.  This field is never {@code null}.
   */
  private final LineNumberReader reader;

  private final ClassLoader classLoader;

  /**
   * A {@link Control} for loading {@link ResourceBundle}s.  This
   * field is never {@code null}.
   */
  private final Control control;

  /**
   * Creates a new {@link MessageFactoryReader}.
   *
   * <p>This constructor calls the {@link
   * #MessageFactoryReader(String, ClassLoader,
   * ResourceBundle.Control)} constructor, passing {@code
   * resourceName} as the first argument and {@code null} for the
   * remaining arguments.</p>
   *
   * @param resourceName the name of a message catalog resource to be
   * loaded via the {@link ClassLoader#getResource(String)} method;
   * must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code resourceName} is
   * {@code null} or identifies a resource that could not be opened
   *
   * @exception IOException if an input or output error occurs
   */
  public MessageFactoryReader(final String resourceName) throws IOException {
    this(resourceName, null, null);
  }

  /**
   * Creates a new {@link MessageFactoryReader}.
   *
   * @param resourceName the name of a resource to be loaded via the
   * {@link ClassLoader#getResource(String)} method; must not be
   * {@code null}
   *
   * @param classLoader the {@link ClassLoader} used to load
   * resources; may be {@code null} in which case the {@linkplain
   * Thread#getContextClassLoader() context <code>ClassLoader</code>}
   * will be used instead
   *
   * @param rbControl a {@link Control} to use when {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader,
   * ResourceBundle.Control) loading <code>ResourceBundle</code>s}; if
   * {@code null} then {@link Control#getControl(List)
   * Control.getControl(Control.FORMAT_DEFAULT)} will be used instead
   *
   * @exception IllegalArgumentException if {@code resourceName} is
   * {@code null} or identifies a resource that could not be opened
   *
   * @exception IOException if an input or output error occurs
   */
  public MessageFactoryReader(final String resourceName, ClassLoader classLoader, final Control rbControl) throws IOException {
    super();
    if (resourceName == null) {
      throw new IllegalArgumentException("resourceName", new NullPointerException("resourceName"));
    }
    if (classLoader == null) {
      classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null) {
        classLoader = this.getClass().getClassLoader();
      }
    }
    assert classLoader != null;
    this.classLoader = classLoader;
    final InputStream resource = this.getResourceAsStream(resourceName);
    if (resource == null) {
      throw new IllegalArgumentException("resourceName", new IllegalStateException("resource not found"));
    }
    this.reader = new LineNumberReader(new BufferedReader(new InputStreamReader(resource)));
    if (rbControl == null) {
      this.control = Control.getControl(Control.FORMAT_DEFAULT);
    } else {
      this.control = rbControl;
    }
  }

  /**
   * Creates a new {@link MessageFactoryReader}.
   *
   * <p>This constructor calls the {@link
   * #MessageFactoryReader(Reader, ClassLoader,
   * ResourceBundle.Control)} constructor, passing {@code reader} as
   * the first argument and {@code null} for the remaining
   * arguments.</p>
   *
   * @param reader the {@link Reader} to read from; must not be {@code
   * null}
   *
   * @exception IllegalArgumentException if {@code reader} is {@code
   * null}
   */
  public MessageFactoryReader(final Reader reader) {
    this(reader, null, null);
  }

  /**
   * Creates a new {@link MessageFactoryReader}.
   *
   * @param reader the {@link Reader} to read from; must not be {@code
   * null}
   *
   * @param classLoader the {@link ClassLoader} used to load
   * resources; may be {@code null} in which case the {@linkplain
   * Thread#getContextClassLoader() context <code>ClassLoader</code>}
   * will be used instead
   *
   * @param rbControl the {@link Control} to use when loading new
   * {@link ResourceBundle}s; if {@code null} then {@link
   * Control#getControl(List)
   * Control.getControl(Control.FORMAT_DEFAULT)} will be used instead
   *
   * @exception IllegalArgumentException if {@code reader} is {@code
   * null}
   */
  public MessageFactoryReader(final Reader reader, ClassLoader classLoader, final Control rbControl) {
    super();
    if (reader == null) {
      throw new IllegalArgumentException("reader", new NullPointerException("reader"));
    }
    if (reader instanceof LineNumberReader) {
      this.reader = (LineNumberReader)reader;
    } else if (reader instanceof BufferedReader) {
      this.reader = new LineNumberReader(reader);
    } else {
      this.reader = new LineNumberReader(new BufferedReader(reader));
    }
    if (classLoader == null) {
      classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null) {
        classLoader = this.getClass().getClassLoader();
      }
    }
    assert classLoader != null;
    this.classLoader = classLoader;
    if (rbControl == null) {
      this.control = Control.getControl(Control.FORMAT_DEFAULT);
    } else {
      this.control = rbControl;
    }
  }

  /**
   * Returns the {@link Control} to use for {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader,
   * ResourceBundle.Control) loading <code>ResourceBundle</code>s}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link Control}
   */
  private final Control getControl() {
    return this.control;
  }

  /**
   * Reads from this {@link MessageFactoryReader}'s {@linkplain
   * #MessageFactoryReader(Reader, ClassLoader,
   * ResourceBundle.Control) affiliated <tt>Reader</tt>} and builds a
   * {@link MessageFactoryReader} from the results.
   *
   * <p>To avoid resource leaks, this {@link MessageFactoryReader}
   * <strong>must</strong> be {@linkplain #close() closed} at some
   * point after this method completes normally.  This method does not
   * call the {@link #close()} method itself.</p>
   *
   * <p>This method consumes a text stream whose contents are a
   * specially formatted message catalog and loads it entirely into
   * memory as a {@link MessageFactory} instance.  The format of such
   * a catalog is detailed below.</p>
   *
   * <p>A message catalog consists of a list of <em>message catalog
   * entries</em> separated by one or more blank lines.</p>
   *
   * <p>A message catalog entry consists of a list of
   * <em>patterns</em>, one per line, followed by a line consisting of
   * two dashes ("{@code --}"), followed by a <em>resource bundle
   * key</em> or unqualified message that those patterns notionally
   * <em>select</em>.</p>
   *
   * <p>A pattern is a textual representation of an {@linkplain
   * Pattern objexj <code>Pattern</code>}.  It resembles a regular
   * expression.  Please see the <a
   * href="http://ljnelson.github.io/objexj/syntax.html">objexj Syntax
   * Guide</a> for more information.</p>
   *
   * <p>A resource bundle key is the name of a classpath resource
   * identifying a {@link ResourceBundle}, followed by a solidus
   * ("{@code /}"), followed by the name of a key within that {@link
   * ResourceBundle}.  Please see the {@linkplain ResourceBundleKey
   * documentation for <code>ResourceBundleKey</code>} for more
   * information.</p>
   *
   * <p>An unqualified message is a simple text string that is not
   * further processed in any way.</p>
   *
   * <p>Line comments may be embedded in the message catalog starting
   * with the hash sign ("{@code #}").</p>
   *
   * <p>Because message catalog entries are processed in order, the
   * most generic message catalog entry should be the last entry in
   * the list.  Typically it will consist of a pattern that matches
   * {@link Throwable} and selects a generic message.</p>
   *
   * <p>Here are some examples of message catalog entries:</p>
   *
   * <blockquote>
   *
   * <pre># Comments like this begin with a hash sign.
   *# Match any {@linkplain Throwable Throwable} chain whose root
   *# cause is a {@linkplain SQLException SQLException}.  If this
   *# {@linkplain Pattern pattern} matches, the corresponding message
   *# can be found in a {@linkplain ResourceBundle} named "com.foo.Messages"
   *# under a {@linkplain ResourceBundle#getObject(String) key} named
   *# databaseError.
   *java.sql.SQLException$
   *--
   *com.foo.Messages/databaseError</pre>
   *
   * <pre># Matches an {@linkplain IllegalArgumentException
   *IllegalArgumentException}
   *# caused by a {@linkplain NullPointerException NullPointerException}
   *# and selects a message named nullArgument in the
   *# com.foo.Messages {@linkplain ResourceBundle ResourceBundle}
   *java.lang.IllegalArgumentException/java.lang.NullPointerException
   *--
   *com.foo.Messages/nullArgument</pre>
   * </blockquote>
   *
   * @param <T> the type of objects new {@link MessageFactory}
   * instances produced from this method will work with
   *
   * @return a new {@link MessageFactory}; never {@code null}
   *
   * @exception IOException if an input or output error occurs
   *
   * @exception IllegalStateException if parsing could not be
   * completed
   *
   * @exception ParseException if the source code could not be parsed
   */
  public <T> MessageFactory<T> read() throws IOException, ParseException {
    assert this.reader != null;
    final MessageFactory<T> mf = new MessageFactory<T>();
    State state = State.NORMAL;
    final Set<Pattern<T>> patterns = new LinkedHashSet<Pattern<T>>();
    StringBuilder message = null;
    String line;
    while ((line = this.reader.readLine()) != null) {
      line = line.trim();

      switch (state) {

        // NORMAL
        //
        // State before processing patterns/matchers.
        // Whitespace legal.
      case NORMAL:
        if (line.isEmpty() || line.startsWith("#")) {
          break;
        } else if (line.startsWith("--")) {
          throw new IllegalStateException("\"--\" is not permitted here at line " + reader.getLineNumber());
        } else {
          state = State.MATCHERS;
          assert patterns.isEmpty();
          assert message == null;
          patterns.add(Pattern.<T>compile(line));
          break;
        }
        // end NORMAL


        // MATCHERS
        //
        // State where we're processing patterns and matchers.
        // Blank lines are illegal.
      case MATCHERS:
        if (line.isEmpty()) {
          throw new IllegalStateException("An empty line is not permitted here at line " + reader.getLineNumber());
        } else if (line.startsWith("#")) {
          break;
        } else if (line.startsWith("--")) {
          state = State.MESSAGE;
        } else {
          patterns.add(Pattern.<T>compile(line));
        }
        break;
        // end MATCHERS


        // MESSAGE
        //
        // The resource bundle key to associate with the patterns just
        // encountered.
      case MESSAGE:
        if (line.isEmpty()) {
          if (message != null) {
            assert message.length() > 0;
            assert !patterns.isEmpty();
            addPatterns(mf, message.toString(), patterns);
            patterns.clear();
            message = null;
          }
          state = State.NORMAL;
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

    }

    if (message != null && !patterns.isEmpty()) {
      addPatterns(mf, message.toString(), patterns);
      patterns.clear();
      message = null;
    }

    return mf;
  }

  private final ClassLoader getClassLoader() {
    return this.classLoader;
  }

  /**
   * {@linkplain MessageFactory#addPatterns(ResourceBundleKey, Set)
   * Adds} the supplied {@link Collection} of {@link Pattern}s to the
   * supplied {@link MessageFactory} under a new {@link
   * ResourceBundleKey} {@linkplain
   * ResourceBundleKey#valueOf(ResourceBundle, Locale,
   * ResourceBundle.Control, String) constructed} from the supplied
   * parameters.
   *
   * @param mf the {@link MessageFactory} to which to add {@link
   * Pattern}s; must not be {@code null}
   *
   * @param defaultResourceBundle the {@linkplain
   * ResourceBundleKey#getResourceBundle() <code>ResourceBundle</code>
   * component} of a {@link ResourceBundleKey}; may be {@code null}
   *
   * @param message the "key" component of a {@link
   * ResourceBundleKey}; must not be {@code null}
   *
   * @param patterns a non-{@code null} {@link Collection} of {@link
   * Pattern}s to add; must not be {@linkplain Collection#isEmpty()
   * empty}
   *
   * @exception IllegalArgumentException if {@code mf}, {@code
   * message} or {@code patterns} is {@code null}, or if {@code
   * patterns} is {@linkplain Collections#isEmpty() emtpy}
   *
   * @exception MissingResourceException if the {@link
   * ResourceBundleKey#valueOf(ResourceBundle, Locale,
   * ResourceBundle.Control, String)} method throws a {@link
   * MissingResourceException}
   *
   * @see ResourceBundleKey#valueOf(ResourceBundle, Locale,
   * ResourceBundle.Control, String)
   *
   * @see MessageFactory#addPatterns(ResourceBundleKey, Collection)
   */
  private final <T> void addPatterns(final MessageFactory<T> mf, final String message, final Collection<? extends Pattern<T>> patterns) {
    if (mf == null) {
      throw new IllegalArgumentException("mf", new NullPointerException("mf"));
    }
    if (message == null) {
      throw new IllegalArgumentException("message", new NullPointerException("message"));
    }
    if (patterns == null) {
      throw new IllegalArgumentException("patterns", new NullPointerException("patterns"));
    }
    if (patterns.isEmpty()) {
      throw new IllegalArgumentException("patterns", new IllegalStateException("patterns.isEmpty()"));
    }
    final ResourceBundleKey rbKey = ResourceBundleKey.valueOf(this.getClassLoader(), this.getControl(), message);
    assert rbKey != null;
    mf.addPatterns(rbKey, patterns);
  }

  /**
   * Closes this {@link MessageFactoryReader} so that it cannot be
   * used again.  Once this method has been called, then the {@link
   * #read()} method will throw an {@link IOException} whenever it is
   * invoked.
   *
   * @exception IOException if an error was encountered during closing
   */
  @Override
  public void close() throws IOException {
    if (this.reader != null) {
      this.reader.close();
    }
  }

  /**
   * Returns a non-{@code null} {@link InputStream} that can read from
   * a classpath resource identified by the supplied {@code
   * resourceName}.
   *
   * <p>This implementation calls the {@link
   * ClassLoader#getResourceAsStream(String)} method on the
   * {@linkplain Thread#getContextClassLoader() context
   * <code>ClassLoader</code>}, if that {@link ClassLoader} is
   * non-{@code null}.  Otherwise, this implementation invokes the
   * {@link Class#getResourceAsStream(String)} method on its own
   * {@link Class}, first ensuring that the supplied {@code resourceName}
   * begins with a {@code /} character.</p>
   *
   * @param resourceName the name of a classpath resource to be
   * opened; must not be {@code null}
   *
   * @return a non-{@code null}, open {@link InputStream}
   *
   * @exception IllegalArgumentException if {@code resourceName} is
   * {@code null} or identifies a resource that could not be opened
   *
   * @see Thread#getContextClassLoader()
   *
   * @see ClassLoader#getResourceAsStream(String)
   *
   * @see Class#getResourceAsStream(String)
   */
  protected InputStream getResourceAsStream(final String resourceName) {
    if (resourceName == null) {
      throw new IllegalArgumentException("resourceName", new NullPointerException("resourceName"));
    }
    final InputStream resource;
    ClassLoader cl = this.getClassLoader();
    if (cl == null) {
      cl = Thread.currentThread().getContextClassLoader();
    }
    if (cl == null) {
      if (resourceName.startsWith("/")) {
        resource = this.getClass().getResourceAsStream(resourceName);
      } else {
        resource = this.getClass().getResourceAsStream(new StringBuilder("/").append(resourceName).toString());
      }
    } else {
      resource = cl.getResourceAsStream(resourceName);
    }
    if (resource == null) {
      throw new IllegalArgumentException("resourceName", new IllegalStateException("resource not found"));
    }
    return resource;
  }

}

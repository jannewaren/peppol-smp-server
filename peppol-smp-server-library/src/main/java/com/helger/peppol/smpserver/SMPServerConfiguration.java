/**
 * Copyright (C) 2015-2017 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package com.helger.peppol.smpserver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.settings.exchange.configfile.ConfigFile;
import com.helger.settings.exchange.configfile.ConfigFileBuilder;

/**
 * The central configuration for the SMP server. This class manages the content
 * of the "smp-server.properties" file. The order of the properties file
 * resolving is as follows:
 * <ol>
 * <li>Check for the value of the system property
 * <code>peppol.smp.server.properties.path</code></li>
 * <li>Check for the value of the system property
 * <code>smp.server.properties.path</code></li>
 * <li>The filename <code>private-smp-server.properties</code> in the root of
 * the classpath</li>
 * <li>The filename <code>smp-server.properties</code> in the root of the
 * classpath</li>
 * </ol>
 * <p>
 * Some of the properties contained in this class can be overwritten in the SMP
 * settings at runtime. That's why the respective direct access methods are
 * deprecated. Use the ones from
 * {@link com.helger.peppol.smpserver.settings.ISMPSettings} instead!
 * </p>
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class SMPServerConfiguration
{
  public static final String KEY_SMP_BACKEND = "smp.backend";
  public static final String KEY_SMP_KEYSTORE_PATH = "smp.keystore.path";
  public static final String KEY_SMP_KEYSTORE_PASSWORD = "smp.keystore.password";
  public static final String KEY_SMP_KEYSTORE_KEY_ALIAS = "smp.keystore.key.alias";
  public static final String KEY_SMP_KEYSTORE_KEY_PASSWORD = "smp.keystore.key.password";
  public static final String KEY_SMP_TRUSTSTORE_PATH = "smp.truststore.path";
  public static final String KEY_SMP_TRUSTSTORE_PASSWORD = "smp.truststore.password";
  public static final String KEY_SMP_FORCE_ROOT = "smp.forceroot";
  public static final String KEY_SMP_PUBLIC_URL = "smp.publicurl";
  public static final String KEY_SMP_IDENTIFIER_TYPE = "smp.identifiertype";
  public static final String KEY_SMP_REST_TYPE = "smp.rest.type";
  public static final String KEY_SMP_REST_WRITABLE_API_DISABLED = "smp.rest.writableapi.disabled";
  public static final String KEY_SMP_PEPPOL_DIRECTORY_INTEGRATION_ENABLED = "smp.peppol.directory.integration.enabled";
  public static final String KEY_SMP_PEPPOL_DIRECTORY_INTEGRATION_AUTO_UPDATE = "smp.peppol.directory.integration.autoupdate";
  public static final String KEY_SMP_PEPPOL_DIRECTORY_HOSTNAME = "smp.peppol.directory.hostname";
  public static final String KEY_SML_ACTIVE = "sml.active";
  public static final String KEY_SML_URL = "sml.url";
  public static final String KEY_SML_SMPID = "sml.smpid";
  public static final String KEY_SML_SMP_IP = "sml.smp.ip";
  public static final String KEY_SML_SMP_HOSTNAME = "sml.smp.hostname";

  public static final boolean DEFAULT_SMP_FORCEROOT = false;
  public static final ESMPIdentifierType DEFAULT_SMP_IDENTIFIER_TYPE = ESMPIdentifierType.PEPPOL;
  public static final ESMPRESTType DEFAULT_SMP_REST_TYPE = ESMPRESTType.PEPPOL;
  public static final boolean DEFAULT_SMP_REST_WRITABLE_API_DISABLED = false;
  public static final boolean DEFAULT_SMP_PEPPOL_DIRECTORY_INTEGRATION_ENABLED = true;
  public static final boolean DEFAULT_SMP_PEPPOL_DIRECTORY_INTEGRATION_AUTO_UPDATE = true;
  public static final String DEFAULT_SMP_PEPPOL_DIRECTORY_HOSTNAME = "https://directory.peppol.eu";
  public static final boolean DEFAULT_SML_ACTIVE = false;

  /**
   * The name of the primary system property which points to the
   * smp-server.properties files
   */
  public static final String SYSTEM_PROPERTY_PEPPOL_SMP_SERVER_PROPERTIES_PATH = "peppol.smp.server.properties.path";
  /**
   * The name of the secondary system property which points to the
   * smp-server.properties files
   */
  public static final String SYSTEM_PROPERTY_SMP_SERVER_PROPERTIES_PATH = "smp.server.properties.path";

  /** The default primary properties file to load */
  public static final String PATH_PRIVATE_SMP_SERVER_PROPERTIES = "private-smp-server.properties";
  /** The default secondary properties file to load */
  public static final String PATH_SMP_SERVER_PROPERTIES = "smp-server.properties";

  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServerConfiguration.class);
  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static ConfigFile s_aConfigFile;

  static
  {
    reloadConfiguration ();
  }

  /**
   * Reload the configuration file. It checks if the system property
   * {@link #SYSTEM_PROPERTY_PEPPOL_SMP_SERVER_PROPERTIES_PATH} or
   * {@link #SYSTEM_PROPERTY_SMP_SERVER_PROPERTIES_PATH} is present and if so,
   * tries it first, than {@link #PATH_PRIVATE_SMP_SERVER_PROPERTIES} is checked
   * and finally the {@link #PATH_SMP_SERVER_PROPERTIES} path is checked.
   *
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess reloadConfiguration ()
  {
    final ConfigFileBuilder aCFB = new ConfigFileBuilder ().addPathFromSystemProperty (SYSTEM_PROPERTY_PEPPOL_SMP_SERVER_PROPERTIES_PATH)
                                                           .addPathFromSystemProperty (SYSTEM_PROPERTY_SMP_SERVER_PROPERTIES_PATH)
                                                           .addPath (PATH_PRIVATE_SMP_SERVER_PROPERTIES)
                                                           .addPath (PATH_SMP_SERVER_PROPERTIES);

    return s_aRWLock.writeLocked ( () -> {
      s_aConfigFile = aCFB.build ();
      if (s_aConfigFile.isRead ())
      {
        s_aLogger.info ("Read PEPPOL SMP server properties from " + s_aConfigFile.getReadResource ().getPath ());
        return ESuccess.SUCCESS;
      }

      s_aLogger.warn ("Failed to read PEPPOL SMP server properties from " + aCFB.getAllPaths ());
      return ESuccess.FAILURE;
    });
  }

  private SMPServerConfiguration ()
  {}

  /**
   * @return The configuration file. Never <code>null</code>.
   */
  @Nonnull
  public static ConfigFile getConfigFile ()
  {
    return s_aRWLock.readLocked ( () -> s_aConfigFile);
  }

  /**
   * @return The backend to be used. Depends on the different possible
   *         implementations. Should not be <code>null</code>. Property
   *         <code>smp.backend</code>.
   * @see com.helger.peppol.smpserver.backend.SMPBackendRegistry
   */
  @Nullable
  public static String getBackend ()
  {
    return getConfigFile ().getAsString (KEY_SMP_BACKEND);
  }

  /**
   * @return The path to the keystore. May be a classpath or an absolute file
   *         path. Property <code>smp.keystore.path</code>.
   */
  @Nullable
  public static String getKeyStorePath ()
  {
    return getConfigFile ().getAsString (KEY_SMP_KEYSTORE_PATH);
  }

  /**
   * @return The password required to open the keystore. Property
   *         <code>smp.keystore.password</code>.
   */
  @Nullable
  public static String getKeyStorePassword ()
  {
    return getConfigFile ().getAsString (KEY_SMP_KEYSTORE_PASSWORD);
  }

  /**
   * @return The alias of the SMP key in the keystore. Property
   *         <code>smp.keystore.key.alias</code>.
   */
  @Nullable
  public static String getKeyStoreKeyAlias ()
  {
    return getConfigFile ().getAsString (KEY_SMP_KEYSTORE_KEY_ALIAS);
  }

  /**
   * @return The password used to access the private key. May be different than
   *         the password to the overall keystore. Property
   *         <code>smp.keystore.key.password</code>.
   */
  @Nullable
  public static char [] getKeyStoreKeyPassword ()
  {
    return getConfigFile ().getAsCharArray (KEY_SMP_KEYSTORE_KEY_PASSWORD);
  }

  /**
   * @return The path to the truststore. May be a classpath or an absolute file
   *         path. Property <code>smp.truststore.path</code>.
   */
  @Nullable
  public static String getTrustStorePath ()
  {
    return getConfigFile ().getAsString (KEY_SMP_TRUSTSTORE_PATH);
  }

  /**
   * @return The password required to open the truststore. Property
   *         <code>smp.truststore.password</code>.
   */
  @Nullable
  public static String getTrustStorePassword ()
  {
    return getConfigFile ().getAsString (KEY_SMP_TRUSTSTORE_PASSWORD);
  }

  /**
   * @return <code>true</code> if all paths should be forced to the ROOT ("/")
   *         context, <code>false</code> if the context should remain as it is.
   *         Property <code>smp.forceroot</code>.
   */
  public static boolean isForceRoot ()
  {
    return getConfigFile ().getAsBoolean (KEY_SMP_FORCE_ROOT, DEFAULT_SMP_FORCEROOT);
  }

  /**
   * @return The server URL that should be used to create absolute URLs inside
   *         the application. This may be helpful when running on a proxied
   *         Tomcat behind a web server. Property <code>smp.publicurl</code>.
   */
  @Nullable
  public static String getPublicServerURL ()
  {
    return getConfigFile ().getAsString (KEY_SMP_PUBLIC_URL);
  }

  /**
   * @return The identifier types to be used. Never <code>null</code>. Defaults
   *         to {@link ESMPIdentifierType#PEPPOL}. Property
   *         <code>smp.identifiertype</code>.
   */
  @Nonnull
  public static ESMPIdentifierType getIdentifierType ()
  {
    final String sType = getConfigFile ().getAsString (KEY_SMP_IDENTIFIER_TYPE);
    return ESMPIdentifierType.getFromIDOrDefault (sType, DEFAULT_SMP_IDENTIFIER_TYPE);
  }

  /**
   * @return The REST type to be used. Never <code>null</code>. Defaults to
   *         {@link ESMPRESTType#PEPPOL}. Property <code>smp.rest.type</code>.
   */
  @Nonnull
  public static ESMPRESTType getRESTType ()
  {
    final String sType = getConfigFile ().getAsString (KEY_SMP_REST_TYPE);
    return ESMPRESTType.getFromIDOrDefault (sType, DEFAULT_SMP_REST_TYPE);
  }

  /**
   * @return The SMP-ID to be used in the SML. Only relevant when SML connection
   *         is active. Property <code>sml.smpid</code>.
   */
  @Nullable
  public static String getSMLSMPID ()
  {
    return getConfigFile ().getAsString (KEY_SML_SMPID);
  }

  /**
   * @return The default IP address to be used for the SML registration (in the
   *         form <code>1.2.3.4</code>). May be <code>null</code> in which case
   *         the name must be manually provided.
   * @since 5.0.3
   */
  @Nullable
  public static String getSMLSMPIP ()
  {
    return getConfigFile ().getAsString (KEY_SML_SMP_IP);
  }

  /**
   * @return The default hostname to be used for the SML registration including
   *         an "http://" prefix as in <code>http://smp.example.org</code>. May
   *         be <code>null</code> in which case the name must be manually
   *         provided.
   * @since 5.0.3
   */
  @Nullable
  public static String getSMLSMPHostname ()
  {
    String ret = getConfigFile ().getAsString (KEY_SML_SMP_HOSTNAME);

    // Ensure prefix
    if (StringHelper.hasText (ret) && !ret.startsWith ("http://"))
      ret = "http://" + ret;
    return ret;
  }
}

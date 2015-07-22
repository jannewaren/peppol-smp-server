/**
 * Copyright (C) 2015 Philip Helger (www.helger.com)
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
package com.helger.peppol.smpserver.rest;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.helger.peppol.identifier.IMutableDocumentTypeIdentifier;
import com.helger.peppol.identifier.IMutableParticipantIdentifier;
import com.helger.peppol.identifier.IdentifierHelper;
import com.helger.peppol.smpserver.SMPServerConfiguration;
import com.helger.peppol.smpserver.restapi.ISMPServerAPIDataProvider;

final class SMPServerAPIDataProvider implements ISMPServerAPIDataProvider
{
  private final UriInfo m_aUriInfo;

  public SMPServerAPIDataProvider (@Nonnull final UriInfo aUriInfo)
  {
    m_aUriInfo = aUriInfo;
  }

  @Nonnull
  public URI getCurrentURI ()
  {
    return m_aUriInfo.getAbsolutePath ();
  }

  @Nonnull
  public String getServiceGroupHref (@Nonnull final IMutableParticipantIdentifier aServiceGroupID)
  {
    UriBuilder aBuilder = m_aUriInfo.getBaseUriBuilder ();
    if (SMPServerConfiguration.isForceRoot ())
    {
      // Ensure that no context is emitted by using "replacePath" first!
      aBuilder = aBuilder.replacePath ("");
    }
    return aBuilder.path (ServiceGroupInterface.class)
                   .buildFromEncoded (IdentifierHelper.getIdentifierURIPercentEncoded (aServiceGroupID))
                   .toString ();
  }

  @Nonnull
  public String getServiceMetadataReferenceHref (@Nonnull final IMutableParticipantIdentifier aServiceGroupID,
                                                 @Nonnull final IMutableDocumentTypeIdentifier aDocTypeID)
  {
    UriBuilder aBuilder = m_aUriInfo.getBaseUriBuilder ();
    if (SMPServerConfiguration.isForceRoot ())
    {
      // Ensure that no context is emitted by using "replacePath" first!
      aBuilder = aBuilder.replacePath ("");
    }
    return aBuilder.path (ServiceMetadataInterface.class)
                   .buildFromEncoded (IdentifierHelper.getIdentifierURIPercentEncoded (aServiceGroupID),
                                      IdentifierHelper.getIdentifierURIPercentEncoded (aDocTypeID))
                   .toString ();
  }
}

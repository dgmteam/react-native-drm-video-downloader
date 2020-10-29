import { useEffect, useState } from 'react';

export const useApp = () => {
  // const [videoUrl, setVideoUrl] = useState<string | undefined>(undefined);
  // const [videoTitle, setVideoTitle] = useState<string | undefined>(undefined);
  // const [keysetId, setKeysetId] = useState<string | undefined>(undefined);
  // const [accessToken, setAccessToken] = useState<string | undefined>(undefined);
  const [videoRequestModel,setVideoRequestModel] = useState<DRMVideoRequestModel | undefined>()
  useEffect(() => {
    setVideoRequestModel({
      id: '03c1f3b4-c93d-4cb9-8c2d-93dc62c18a64',
      licenseUrl: 'https://mvvuni.keydelivery.southeastasia.media.azure.net/Widevine/?kid=03c1f3b4-c93d-4cb9-8c2d-93dc62c18a64',
      url:
        'https://mvvuni-aase.streaming.media.azure.net/b33a9ffa-0696-41e2-9574-e5b87dff0f75/Big-Buck-Bunny-30s.ism/manifest(format=mpd-time-csf,encryption=cenc)',
      scheme: 'widevine',
      drmLicenseRequestHeaders: {
        Authorization: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cm46bWljcm9zb2Z0OmF6dXJlOm1lZGlhc2VydmljZXM6Y29udGVudGtleWlkZW50aWZpZXIiOiIwM2MxZjNiNC1jOTNkLTRjYjktOGMyZC05M2RjNjJjMThhNjQiLCJuYmYiOjE2MDE5ODI4OTYsImV4cCI6MTYwMTk4Njc5NiwiaXNzIjoiaHR0cHM6Ly9ldmVybGVhcm4udm4iLCJhdWQiOiJ3ZWJhcHAifQ.R6wuhZe6gGYDrbsOWmm13dSlniu2aYqNYp4HU_ixBTQ',
      },
      title:'Demo video'
    })
  },[

  ])
  return {
    videoRequestModel,
    setVideoRequestModel
  };
};

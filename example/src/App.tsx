import * as React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import DrmVideoDownloader from 'react-native-drm-video-downloader';
import { useApp } from './hooks/useApp';

export default function App() {
  const AppHook = useApp();

  // React.useEffect(() => {
  //   DrmVideoDownloader.registerTrackingEvent();
  //   return () => {
  //     DrmVideoDownloader.unregisterTrackingEvent();
  //   };
  // }, []);

  return (
    <View>
      <TextItem
        leftText={'Name'}
        rightText={AppHook.videoRequestModel?.title}
      />
      <TextItem leftText={'Url'} rightText={AppHook.videoRequestModel?.url} />
      <TouchableOpacity
        style={[
          {
            marginTop: 16,
            padding: 8,
            backgroundColor: 'gray',
            justifyContent: 'center',
            alignContent: 'center',
            alignItems: 'center',
          },
        ]}
        onPress={AppHook.controlDownloadVideo}
      >
        <Text>{AppHook.getButtonText()}</Text>
      </TouchableOpacity>

      <Text
        style={[
          {
            marginTop: 16,
            fontSize: 16,
          },
        ]}
      >
        {AppHook.getProgressText()}
      </Text>
    </View>
  );
}

export const TextItem = (props?: { leftText?: string; rightText?: string }) => {
  return (
    <View
      style={[
        {
          flexDirection: 'row',
          marginVertical: 8,
        },
      ]}
    >
      <Text>{`${props?.leftText}: `}</Text>
      <Text>{props?.rightText}</Text>
    </View>
  );
};

// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//     alignItems: 'center',
//     justifyContent: 'center',
//   },
// });
import * as React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import DrmVideoDownloader from 'react-native-drm-video-downloader';
import { useApp } from './hooks/useApp';

export default function App() {
  const { videoRequestModel } = useApp();

  const download = () => {
    console.log('request download video');
    DrmVideoDownloader.download(
      videoRequestModel,
      (download?: any) => {
        console.log('result', download);
      },
      (error?: any) => {
        console.log('error', error);
      }
    );
  };

  return (
    <View>
      <TextItem leftText={'Name'} rightText={videoRequestModel?.title} />
      <TextItem leftText={'Url'} rightText={videoRequestModel?.url} />
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
        onPress={download}
      >
        <Text>{'Download'}</Text>
      </TouchableOpacity>
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

/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  View,
  TextInput,
  ActivityIndicator,
  Input,
  AsyncStorage,
  ToolbarAndroid,
  Navigator,
} from 'react-native';

import { Header,Container,Title, Form, Item, Label, Content, List, ListItem, InputGroup, Icon, Text, Picker, Button } from 'native-base';

import * as firebase from 'firebase';

import Login from './screens/login';
import UserData from './screens/userData';
import Main from './screens/main';

const firebaseConfig = {
  apiKey: "AIzaSyDdOLBVS7RBio1F_wM8KQk17jAq5m_h49M",
  authDomain: "projektcarcare.firebaseapp.com",
  databaseURL: "https://projektcarcare.firebaseio.com",
  storageBucket: "gs://projektcarcare.appspot.com",
};
const firebaseApp = firebase.initializeApp(firebaseConfig);

export default class Carcare extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    const routes = [
        {name: 'login'},
      ];

      return (
      <Navigator
        initialRoute={routes[0]}
        renderScene={(route, navigator) => {

                  switch(route.name) {
                    case 'userData':
                      return <UserData nav={navigator} fb={firebaseApp} />;
                      break;
                    case 'main':
                      return <Main nav={navigator} fb={firebaseApp} />;
                      break;
                    case 'login':
                    default:
                      return <Login nav={navigator} fb={firebaseApp} />;
                    }
                  }
                }
      />
      );
  }
}

const styles = StyleSheet.create({
  navigator: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#ffffff',
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#ffffff',
  },
});

AppRegistry.registerComponent('Carcare', () => Carcare);

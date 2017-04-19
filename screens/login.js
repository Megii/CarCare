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

import { Header,Container,Title, Form, Item, Label, Content, List, ListItem, Left, Right, Body, InputGroup, Icon, Text, Picker, Button } from 'native-base';

import PushController from "../utils/PushController";
import firebaseClient from  "../utils/FirebaseClient";

export default class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      email: 'test2@test.pl',
      pass: 'trudnehaslo',
      content: null,
    };

    this.onLogin = this.onLogin.bind(this);
    this.onRegister = this.onRegister.bind(this);
  }

  onLogin() {
    if(this.state.email && this.state.pass) {
      this.setState({loading : true});
      this.props.fb.auth().signInWithEmailAndPassword(this.state.email, this.state.pass)
      .then((userData) =>
      {
        AsyncStorage.setItem('userData', JSON.stringify(userData));
        const userId = this.props.fb.auth().currentUser.uid;

        this.props.fb.database().ref('/users/' + userId).once('value').then(function(data) {
          this.setState({
                loading: false,
              });
          if(data.val() != null) {
            this.props.nav.push({
              name: 'main',
            });
          } else {
            this.props.nav.push({
              name: 'userData',
            });
          }
        }.bind(this))
      }
    ).catch((error) =>
        {
              this.setState({
                loading: false
              });
        alert('Login Failed. Please try again'+error);
    });
    }
  };

  onRegister() {
    if(this.state.email && this.state.pass) {
      this.setState({loading : true});
      this.props.fb.auth().createUserWithEmailAndPassword(this.state.email, this.state.pass)
      .then((userData) =>
      {
        AsyncStorage.setItem('userData', JSON.stringify(userData));
        this.setState({
                loading: false,
              });
        this.props.nav.push({
          name: 'userData'
        });
      }
    ).catch((error) =>
        {
              this.setState({
                loading: false
              });
        alert('Register Failed. Please try again'+error);
    });
    }
  };

  render() {
    let content = this.state.loading ?
      <View style={styles.loading}>
        <ActivityIndicator size="large"/>
      </View> :
        this.state.content ? this.state.content :
      <View style={styles.container}>
      <PushController
          onChangeToken={token => this.setState({token: token || ""})}/>
          <Text>Email:</Text>
          <TextInput
            style={styles.form}
            onChangeText={(email) => this.setState({email})}
            value={this.state.email} />
          <Text>Hasło:</Text>
          <TextInput
            style={styles.form}
            onChangeText={(pass) => this.setState({pass})}
            value={this.state.pass}
            secureTextEntry={true}
          />
          <View style={styles.buttons}>
            <Button
              iconLeft
              onPress={this.onLogin}
              marginHorizontal={10}
            >
              <Icon name='key' />
              <Text>Logowanie</Text>
            </Button>
            <Button
              iconLeft
              onPress={this.onRegister}
              marginHorizontal={10}
              >
              <Icon name='person-add' />
              <Text>Rejestracja</Text>
            </Button>
        </View>
      </View>;

      return (<Container>
                            <Header>
                                <Body>
                                    <Title>Logowanie</Title>
                                </Body>
                           </Header>
                      <View style={styles.content}>
                      {content}
                      </View>
                    </Container>);
  }
};

const styles = StyleSheet.create({
  loading: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#ffffff',
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#ffffff',
  },
  container: {
    width: 300,
    flex: 1,
    justifyContent: 'center',
    alignItems: 'flex-start',
    backgroundColor: '#ffffff',
  },
  form: {
    width: 300,
    height: 40,
    marginBottom: 10,
  },
  buttons: {
    width: 300,
    marginTop: 20,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
});
import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  View,
  TextInput,
  ActivityIndicator,
  AsyncStorage,
  Input,
} from 'react-native';

import { Header,Container,Title, Form, Item, Label, Content, List, ListItem, Left, Right, Body, InputGroup, Icon, Text, Picker, Button } from 'native-base';

export default class UserData extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nav: this.props.nav,
      nr: '',
      color: '',
      model: '',
    };

    this.onSave = this.onSave.bind(this);
  }

  onSave() {
    if(this.state.color && this.state.nr && this.state.model) {
      const userId = this.props.fb.auth().currentUser.uid;
      this.props.fb.database().ref('users/' + userId).set({
        nr: this.state.nr,
        color: this.state.color,
        model : this.state.model,
      }).then(() => {
        this.props.nav.push({
                name: 'main',
              });
      });
    }
  }

  render() {
    return (
    <Container>
                  <Header>
                    <Body>
                        <Title>Dane samochodu</Title>
                    </Body>
                  </Header>
    <View style={styles.content}>
    <View style={styles.container}>
          <Text>Nr rejestracyjny:</Text>
          <TextInput
            style={styles.form}
            onChangeText={(nr) => this.setState({nr})}
            value={this.state.nr} />
          <Text>Kolor:</Text>
          <TextInput
            style={styles.form}
            onChangeText={(color) => this.setState({color})}
            value={this.state.color}
          />
          <Text>Marka:</Text>
          <TextInput
            style={styles.form}
            onChangeText={(model) => this.setState({model})}
            value={this.state.model}
          />
          <Button iconLeft onPress={this.onSave} >
              <Icon name='checkmark' />
              <Text>Zapisz</Text>
            </Button>
    </View>
    </View>
    </Container>);
  }
};

const styles = StyleSheet.create({
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

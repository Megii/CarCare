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
import { ColorPicker, fromHsv, toHsv } from 'react-native-color-picker';

import { Header,Container,Title, Form, Item, Label, Content, List, ListItem, Left, Right, Body, InputGroup, Icon, Text, Picker, Button } from 'native-base';

export default class UpdateUserData extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nav: this.props.nav,
      nr: '',
      color: '',
      model: '',
    };

    this.onUpdate = this.onUpdate.bind(this);
    this.onColorChange = this.onColorChange.bind(this);
  }

  componentDidMount(){
    const userId = this.props.fb.auth().currentUser.uid;
    this.props.fb.database().ref('/users/' + userId).once('value').then(function(data){
      this.setState({
        nr: data.val().nr,
        color : data.val().color,
        model : data.val().model,
      })
    }.bind(this)
  )
  }

  onColorChange(color) {
    console.log(color);
    this.setState({
      color: fromHsv(color),
    });
  }

  onUpdate() {
    if(this.state.color || this.state.nr || this.state.model) {
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
                  <Header androidStatusBarColor="#141829" style={{ backgroundColor: '#141829'}} >
                    <Body>
                        <Title>Dane samochodu</Title>
                    </Body>
                  </Header>
    <View style={styles.content}>
    <View style={styles.container}>
          <Text style={{color: '#fff'}}>Nr rejestracyjny:</Text>
          <TextInput maxLength={8}
            style={styles.form}
            onChangeText={(nr) => this.setState({nr})}
            value={this.state.nr} />
          <Text style={{color: '#fff'}}>Marka:</Text>
          <TextInput
            style={styles.form}
            onChangeText={(model) => this.setState({model})}
            value={this.state.model}
          />
          <Text style={{color: '#fff'}}>Kolor:</Text>
          <View style={{height: 140, flexDirection: 'column', justifyContent: 'center', alignItems: 'center'}}>
              <View style={{height: 120, justifyContent: 'center', alignItems: 'center'}}>
                      <ColorPicker
                        color={this.state.color}
                        onColorSelected={color => alert(`Color selected: ${color}`)}
                        onColorChange={this.onColorChange}
                        style={{flex: 1, height: 100, width: 150, justifyContent: 'center'}}
                    />
              </View>
          </View>
          <Button iconLeft onPress={this.onUpdate} style={{backgroundColor: '#21294C'}} >
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
    backgroundColor: '#141829',
  },
  container: {
    width: 300,
    flex: 1,
    justifyContent: 'center',
    alignItems: 'flex-start',
    backgroundColor: '#141829',
  },
  form: {
    width: 300,
    height: 40,
    marginBottom: 20,
    color: '#fff',
    borderColor: '#fff',
    backgroundColor: '#21294C'
  },
  buttons: {
    width: 300,
    marginTop: 20,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
});

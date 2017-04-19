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

import { Header,Container,Title, Form, Item, Label, Content, Body, List, ListItem, Left, Right, InputGroup, Icon, Text, Picker, Button } from 'native-base';

import PushController from "../utils/PushController";
import firebaseClient from  "../utils/FirebaseClient";

export default class Main extends Component {
  constructor(props) {
    super(props);
    this.state = {
      users: null,
      token: '',
    };

    this.onSelectUser = this.onSelectUser.bind(this);
    this.onBtnPress = this.onBtnPress.bind(this);
    this.onNewRecord = this.onNewRecord.bind(this);
  }

  componentDidMount() {
    const usersRef = this.props.fb.database().ref('/users');
        usersRef.on('value', function(element) {
          // this.updateUserList(element.val());
          let tempUsers = element.val()
          _.forEach(element.val(), (val, key) => {
            tempUsers[key].checked = false;
          });
          this.setState({users: tempUsers});
          console.log(this.state.users);
        }.bind(this));

        // usersRef.once('value').then(function(snapshot) {
        //   console.log(snapshot.val());
        //   this.setState({users: snapshot.val()});
        // }.bind(this));
  }

  // updateUserList(a, b) {
  //   console.log(a, b);
  // }
  //

  onSelectUser(user) {
    _.forEach(this.state.users, (val, key) => {
      let index  = this.state.users[key].nr == user.nr ? key : false;

      if(index) {
        let tempUsers = this.state.users;
        tempUsers[index].checked = !tempUsers[index].checked;
        this.setState({ users: tempUsers });
      }

      console.log(this.state.users);
    });
  }

  onBtnPress(msg) {
    alert(msg);

    let to = [];

    for(x in this.state.users) {
      to.push(x);
    }

    this.props.fb.database().ref(`messages/${new Date().getTime()}`).set({
        from: this.props.fb.auth().currentUser.uid,
        msg: msg,
        to : to,
      })
  }

  onNewRecord() {
    alert('Nowe nagranie głosowe');
  }

  render() {
  let { token } = this.state;
  const list = !this.state.users ? <ActivityIndicator size="large"/> :
                        <List style={{flex: 1}} dataArray={this.state.users}
                                                renderRow={(user) =>
                                                    <ListItem button onPress={()=>this.onSelectUser(user)}>
                                                    <Left>
                                                        <Text>{user.nr} {user.color} {user.model} {user.checked} </Text>
                                                    </Left>
                                                    <Body />
                                                    <Right>
                                                      <Icon name="checkmark" style={{ opacity: user.checked ? 1 : 0, }} />
                                                    </Right>
                                                    </ListItem>

                                                }>
                                            </List>;
    return (
    <Container>
                  <Header>
                      <Body>
                        <Title>CarCare</Title>
                      </Body>
                  </Header>
    <View style={styles.container}>
          <View style={styles.half2}>
            <Content>
              {list}
            </Content>
          </View>
          <View style={styles.half}>
          <View style={styles.buttons}>
            <Button rounded large onPress={() => this.onBtnPress('Awiaria oświetlenia')}>
              <Icon name='bulb' />
            </Button>
            <Button rounded large onPress={() => this.onBtnPress('Wyciek płynów')}>
              <Icon name='water' />
            </Button>
            <Button rounded large onPress={() => this.onBtnPress('Awaria układu jezdnego')}>
              <Icon name='car' />
            </Button>
          </View>
          <View style={styles.buttons}>
            <Button rounded large onPress={() => this.onBtnPress('Prace drogowe')}>
              <Icon name='warning' />
            </Button>
            <Button rounded large onPress={() => this.onBtnPress('Wypadek')}>
              <Icon name='flash' />
            </Button>
            <Button rounded danger large onPress={() => this.onNewRecord()}>
              <Icon name='mic' />
            </Button>
          </View>
          </View>
    </View>
    </Container>);
  }
};

class Checked extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (this.props.isChecked ?  <Icon name='checkmark' /> : false);
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff',
  },
  half2: {
    flex: 2,
  },
  half: {
    flex: 1,
  },
  list: {
    flex: 1,
  },
  buttons: {
    padding: 10,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-around',
  }
});

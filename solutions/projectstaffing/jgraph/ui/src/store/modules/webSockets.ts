import { Module } from 'vuex';
import { Client, IFrame } from '@stomp/stompjs';

const webSockets: Module<any, any> = {
  state: {
    wsInstance: null,
    wsConnected: false,
    wsDisconnected: false
  },
  getters: {},
  mutations: {
    SET_WS_STATE(state, value) {
      state.wsConnected = value;
    },
    SET_WS_DISCONNECTED_STATE(state, value) {
      state.wsDisconnected = value;
    }
  },
  actions: {
    initWS(context) {
      context.state.wsInstance = new Client({
        brokerURL: `${location.protocol === 'https:' ? 'wss' : 'ws'}://${
          location.host
        }/gdc-topic`,
        onConnect: (receipt: IFrame) => {
          console.log('Websocket connected');
          context.commit('SET_WS_STATE', true);
        },
        debug: ev => {
          console.log(ev);
        },
        onDisconnect: () => {
          context.commit('SET_WS_STATE', false);
          console.log('Websocket disconnected');
        },
        onStompError: (err: any) => {
          console.log(err);
          context.commit('SET_WS_STATE', false);
        },
        onChangeState: (state: any) => {
          console.log(state);
        },
        onWebSocketError: (err: any) => {
          console.log(err);
          context.commit('SET_WS_STATE', false);
        },
        onWebSocketClose: (closeEvent: any) => {
          context.commit('SET_WS_DISCONNECTED_STATE', true);
          console.log(closeEvent);
          context.commit('SET_WS_STATE', false);
        },
        reconnectDelay: 10000,
        heartbeatIncoming: 1000,
        heartbeatOutgoing: 1000
      });
      context.state.wsInstance.activate();
    }
  }
};

export default webSockets;

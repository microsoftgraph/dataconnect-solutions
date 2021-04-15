export default {
  methods: {
    dispatch(componentName, eventName, that, ...rest) {
      let parent = that.$parent || that.$root;
      let name = parent.$options.name;

      while (parent && (!name || name !== componentName)) {
        parent = parent.$parent;
        if (parent) {
          name = parent.$options.name;
        }
      }

      if (parent) {
        parent.$emit.apply(parent, [eventName].concat(rest));
      }
    }
  }
};

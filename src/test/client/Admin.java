package test.client;

import com.google.gwt.core.client.JavaScriptObject;
class Admin extends JavaScriptObject
{
   protected Admin()
   {}
   public final native int getID()
   /*-{
      return this.admin.id;
   }-*/;
   public final native String username()
   /*-{
      return this.admin.username;
   }-*/;
   public final native String password()
   /*-{
      return this.admin.password;
   }-*/;
}
